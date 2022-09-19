<template>
    <div class="countdown">
        <div class="countdownNum">
           3
        </div>
    </div>
</template>

<script>

const Begin_countdown = 3;
export default {
    setup(){
        const countDownTime = () => {
            var num = document.querySelector(".countdownNum");
            console.log(num);
            let time = Begin_countdown;
            const interval_id = setInterval(() => {
                if(time !== 0){
                    num.innerHTML = time;
                    time --;
                }else{
                    clearInterval(interval_id);
                }
            }, 1000);
        }
        countDownTime();
    },
}
</script>

<style scoped>
div.countdown{
    height: 40vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
}

div.countdown-num{
    text-align: center;
    color: white;
    font-size: 50px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
}
</style>