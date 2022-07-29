import { GameObject } from "./GameObject";
import { Wall } from "./Wall";

export class GameMap extends GameObject{
    // ctx: canvas; parent of canvas: dynamically modify the length and width of the canvas
    constructor(ctx, parent){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0; // absolute distance of a grid in the game map

        this.rows = 13; // the game map is a 13*13 grid board
        this.cols = 13;

        this.inner_walls_count = 20;
        this.walls = [];
    }

    check_connectivity(g, sx, sy, tx, ty){ // status, source x y, target x y
        // DFS Algorithm to check the connectivity
        if(sx == tx && sy == ty)
            return true;
        g[sx][sy] = true;

        let dx = [-1,0,1,0];
        let dy = [0,1,0,-1];

        for(let i = 0; i < 4; i++){
            let x = sx + dx[i];
            let y = sy + dy[i];

            if(!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }

        return false;
    }

    create_walls(){
        const g = [];
        for(let r = 0; r < this.rows; r++){
            g[r] = [];
            for(let c = 0; c < this.cols; c ++){
                g[r][c] = false;
            }
        }

        // Fence the four sides
        for(let r = 0; r < this.rows; r++){
            g[r][0] = g[r][this.cols - 1] = true;
        }

        for(let c = 0; c < this.cols; c++){
            g[0][c] = g[this.rows - 1][c] = true;
        }

        // Create random obstacles (axisymmetry and centrosymmetry)
        for(let i=0 ; i < this.inner_walls_count / 2; i++){
            // Cycle 1000 times to prevent the position of a wall from repeating
            for(let j = 0; j < 1000; j++){
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if(g[r][c] || g[c][r]) // dupicate walls for one grid
                    continue;
                if(r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) // The grids in the lower left and upper right corners have no walls
                    continue;

                g[r][c] = g[c][r] = true;
                break;
            }
        }

        const copy_g = JSON.parse(JSON.stringify(g)); // copy current status

        // The grids in the lower left corner and the upper right corner must be connected
        if(!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2))
            return false;

        for(let r = 0; r < this.rows; r++){
            for(let c = 0; c < this.cols; c ++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }

        return true;
    }

    start(){
        // Cycle 1000 times to ensure that the lower left corner and the upper right corner of the generated map are connected
        for(let i = 0; i < 1000; i++){
            if(this.create_walls())
                break;
        }
    }

    update_size(){
        // game map is a square， but the playground's size will be changed with the change of browser, so need update every frame
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update(){
        // update the canvas size(the game map size) every frame
        this.update_size();
        this.render();
    }

    render(){
        const color_even = "#AAD751", color_odd = "#A2D149";
        // dye each grid of the board
        for(let r = 0; r < this.rows; r++){
            for(let c = 0; c < this.cols; c++){
                if((r + c) % 2 == 0){
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }
                // parameters: distance from upper, left boundary, the width, height of the grid
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}