<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{$store.state.user.username}}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>Manual Operation</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">{{bot.title}}</option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    {{$store.state.pk.opponent_username}}
                </div>
            </div>
        </div>
        <div class="col-12" style="text-align: center; padding-top: 10vh">
            <button @click="click_match_btn" v-if="match_btn_info === 'begin'" class="btn btn-success" style="height: 10vh; width: 15vw; font-size: 3vh">Begin Matching</button>
            <button @click="click_match_btn" v-if="match_btn_info === 'cancel'" class="btn btn-warning" style="height: 10vh; width: 15vw; font-size: 3vh">Cancel Matching</button>
        </div>
    </div>
</template>

<script>

import { ref } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'

export default{
    components: {
       
    },

    setup(){
        let match_btn_info = ref("begin");

        const store = useStore();

        let bots = ref([]);
        let select_bot = ref(-1);

        const click_match_btn = () => {
            if(match_btn_info.value === "begin"){
                match_btn_info.value = "cancel";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id: select_bot.value,
                }))
            }else{
                match_btn_info.value = "begin";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }))
            }
        }

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    bots.value = resp;
                },
            })
        } 

        refresh_bots();

        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }
}
</script>

<style scoped>
div.matchground{
    width: 60vw;
    height: 70vh;
    background: lightblue;
    margin: 40px auto;
    background-color: rgba(50,50,50,0.5);
}

div.user-photo{
    text-align: center;
    padding-top: 10vh;
}

div.user-photo > img{
    border-radius: 50%;
    width: 20vh;
}

div.user-username{
    text-align: center;
    font-size: 30px;
    font-weight: 600;
    color: #fff;
    padding-top: 2vh;
}

div.user-select-bot{
    padding-top: 20vh;
}

div.user-select-bot > select{
    width: 80%;
    margin: 0 auto;
}


</style>