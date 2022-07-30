export class Cell{
    constructor(r,c){
        this.r = r;
        this.c = c;
        // The coordinate axis of canvas: left to right is x up to down is y
        this.x = this.c + 0.5;
        this.y = this.r + 0.5;
    }
}