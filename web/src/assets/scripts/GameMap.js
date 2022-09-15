import { GameObject } from "./GameObject";
import { Wall } from "./Wall";
import { Snake } from './Snake';

export class GameMap extends GameObject{
    // ctx: canvas; parent of canvas: dynamically modify the length and width of the canvas
    constructor(ctx, parent, store){
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0; // absolute distance of a grid in the game map, the coordinate (x,y) is relative distance, so must *L

        // 13*14 —— the starting points of two players are (1,13) and (12,1) seperately, the head of 2 players will not enter a grid - died at the same time
        this.rows = 13; // the game map is a 13*14 grid board
        this.cols = 14;

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ]
    }


    create_walls(){
        const g = this.store.state.pk.gamemap;
        for(let r = 0; r < this.rows; r++){
            for(let c = 0; c < this.cols; c ++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }
    }

    add_listening_events(){
        this.ctx.canvas.focus();

        // user0: wdsa; user1: direction key
        const [snake0 ,snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if(snake0.status !== "die" && snake1.status !== "die"){
                console.log(e.key);
                if(snake0.status !== "move"){
                    if(e.key === 'w')
                        snake0.set_direction(0);
                    else if(e.key === 'd')
                        snake0.set_direction(1);
                    else if(e.key === 's')
                        snake0.set_direction(2);
                    else if(e.key === 'a')
                        snake0.set_direction(3);
                }
                if(snake1.status !== "move"){
                    if(e.key === 'ArrowUp')
                        snake1.set_direction(0);
                    else if(e.key === 'ArrowRight')
                        snake1.set_direction(1);
                    else if(e.key === 'ArrowDown')
                        snake1.set_direction(2);
                    else if(e.key === 'ArrowLeft')
                        snake1.set_direction(3);
                }
            }
        })
    }

    start(){
        this.create_walls();
        this.add_listening_events();
    }

    update_size(){
        // the playground's size will be changed with the change of browser, so need update every frame
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    // Judge whether both snakes are ready for the next round
    // check_ready(){
    //     for(const snake of this.snakes){
    //         if(snake.status !== "idle")
    //             return false;
    //         if(snake.direction === -1)
    //             return false;
    //     }
    //     return true;
    // }

    // 2 snakes all enter the next round
    next_step(){
        for(const snake of this.snakes)
            snake.next_step();
    }

    // check whether the snake meet the walls or another snake's body
    check_valid(cell){
        for(const wall of this.walls){
            if(wall.r === cell.r && wall.c === cell.c)
                return false;
        }

        for(const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()){ // 当蛇的长度不会改变时，即蛇尾会改变位置时，蛇尾的位置是下一步可以走的
                k--;
            }
            for(let i = 0; i < k; i++){
                if(snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    update(){
        // update the canvas size(the game map size) every frame
        this.update_size();
        //if(this.check_ready()){
        this.next_step();
        //}
        this.render();
    }

    render(){
        const color_even = "#AAD751", color_odd = "#A2D149";
        // dye each grid of the board
        for(let r = 0; r < this.rows; r++){
            for(let c = 0; c < this.cols; c++){
                if((r + c) % 2 === 0){
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