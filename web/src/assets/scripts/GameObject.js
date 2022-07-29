const GAME_OBJECTS = []


export class GameObject {
    constructor(){
        GAME_OBJECTS.push(this);
        this.timedelta = 0; // the time interval between 2 frames' execution
        this.has_called_start = false;
    }

    // execute one time
    start(){

    }

    // execute in every frame except the first one
    update(){

    }

    // execute before destory
    on_destory(){

    }

    destory(){
        this.on_destory();

        for(let i in GAME_OBJECTS){ 
            const obj = GAME_OBJECTS[i];
            if(obj === this){
                GAME_OBJECTS.splice(i); // delete
                break;
            }
        }
    }
}

let last_timestamp; // last execution time
const step = timestamp => {
    for(let obj of GAME_OBJECTS){
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    /* execute the "step" function in every frame (render 60 frames every second) */
    requestAnimationFrame(step)
}

/* the web will execute the "step" func before the render of the first frame */
requestAnimationFrame(step)