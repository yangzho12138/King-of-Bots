<template>
    <div class="matchground">
        <div class="row">
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{$store.state.user.username}}
                </div>
            </div>
            <div class="col-6">
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

export default{
    components: {
       
    },

    setup(){
        let match_btn_info = ref("begin");

        const store = useStore();

        const click_match_btn = () => {
            if(match_btn_info.value === "begin"){
                match_btn_info.value = "cancel";
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                }))
            }else{
                match_btn_info.value = "begin";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }))
            }
        }

        return {
            match_btn_info,
            click_match_btn,
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


</style>