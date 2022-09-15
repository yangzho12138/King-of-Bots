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

        // offset of every step
        this.dr = [-1, 0, 1, 0];
        this.dc = [0, 1, 0, -1];

        // rounds
        this.step = 0;
        // allowable error
        this.eps = 1e-2;

        // 左下角的蛇初始朝上，右上角的蛇初始朝下
        this.eye_direction = 0;
        if(this.id === 1)
            this.eye_direction = 2;
        
        // offset of snake's eyes
        this.eye_dx = [
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];

    }

    start(){

    }

    set_direction(d){
        this.direction = d;
    }

    // Check whether the length of the snake in the current round needs to be increased
    check_tail_increasing(){
        if(this.step <= 10) 
            return true;
        if(this.step % 3 === 1)
            return true;
        return false;
    }

    next_step(){
        const d = this.direction;
        if(d === -1)
            return false;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;
        this.status = "move";
        this.step ++;

        // 蛇的移动，中间不变，尾部消失，在前进的方向上接上一个头
        const k = this.cells.length;
        for(let i = k; i > 0; i --){
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1])); // Deep copy, create a new object
        }

        if(!this.gamemap.check_valid(this.next_cell)) // the operation result in the death of snake
            this.status = "die";

    }

    update_move(){
        const move_distance = this.speed * this.timedelta / 1000; // the walking distance of one frame

        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        console.log(this.next_cell.x, this.cells[0].x, this.next_cell.y, this.cells[0].y);
        const distance = Math.sqrt(dx * dx + dy * dy);

        if(distance < this.eps){ // arrive the target point
            this.status = "idle";
            this.cells[0] = this.next_cell;
            this.next_cell = null;

            if(!this.check_tail_increasing()) // 蛇不变长，砍掉尾巴
                this.cells.pop();
        }else{
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            // 蛇的长度不增加，蛇尾需要往前移动
            if(!this.check_tail_increasing()){
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
        if(this.status == "die"){
            ctx.fillStyle = "white"; // 蛇死亡
        }

        for(const cell of this.cells){
            // 画圆
            ctx.beginPath();
            // the x,y position of the circle, the radius, start angle and end angle
            // 在渲染的时候将蛇的宽度 * 0.8 视觉效果更好，被注释掉的为原本的写法
            // ctx.arc(cell.x * L, cell.y * L, L/2, 0, Math.PI * 2);
            ctx.arc(cell.x * L, cell.y * L, L/2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for(let i = 1; i < this.cells.length; i++){
            const a = this.cells[i-1];
            const b = this.cells[i];
            if(Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) // 两个球已经重合(蛇尾)
                continue;
            else if(Math.abs(a.x - b.x) < this.eps) // 两个球处于纵向位置
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
                //ctx.fillRect((a.x - 0.5) * L, Math.min(a.y, b.y) * L, L, Math.abs(a.y - b.y) * L);
            else
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
                //ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.5) * L, Math.abs(a.x - b.x) * L, L);
        }

        ctx.fillStyle = "black";
        // 枚举左眼和右眼
        for(let i = 0; i < 2; i++){
            // 画圆
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.25) * L; // 0.25是眼睛相较于园中心的偏移量
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.25) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.1, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}