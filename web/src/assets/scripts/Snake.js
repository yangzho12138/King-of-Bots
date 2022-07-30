import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject{
    // the info of a snake
    constructor(info, gamemap){
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)]; // store the body of a snake, cells[0] is the head
        this.next_cell = null; // the destination of next step

        this.speed = 5;  // The snake walks five grids per second
        this.direction = -1; // Direction command received by snake 0-up, 1-right, 2-down, 3-left
        this.status = "idle"; // idle, move, die

        // offser of every step
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];

        // rounds
        this.step = 0;
        // allowable error
        this.eps = 1e-2;
    }

    start(){

    }

    set_direction(d){
        this.direction = d;
    }

    // Check whether the length of the snake in the current round needs to be increased
    check_tail_direction(){
        if(this.step <= 10) 
            return true;
        if(this.step % 3 === 1)
            return true;
        return false;
    }

    next_step(){
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.direction = -1;
        this.status = "move";
        this.step ++;

        // 蛇的移动，中间不变，尾部消失，在前进的方向上接上一个头
        const k = this.cells.length;
        for(let i = k; i > 0; i --){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1])); // Deep copy, create a new object
        }
    }

    update_move(){
        const move_distance = this.speed * this.timedelta / 1000; // the walking distance of one frame

        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance < this.eps){ // arrive the target point
            this.status = "idle";
            this.cells[0] = this.next_cell;
            this.next_cell = null;

            if(!this.check_tail_direction()) // 蛇不变长，砍掉尾巴
                this.cells.pop();
        }else{
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            // 蛇的长度不增加，蛇尾需要往前移动
            if(!this.check_tail_direction()){
                const k = this.cells.length;
                const tail = this.cells[k-1]; // 浅拷贝，tail变了，this.cells[k-1]也会变
                const tail_target = this.cells[k-2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
        
    }

    update(){ // execute once every step
        if(this.status === 'move')
            this.update_move();
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        for(const cell of this.cells){
            ctx.beginPath();
            // the x,y position of the circle, the radius, start angle and end angle
            ctx.arc(cell.x * L, cell.y * L, L/2, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}