<template>
   <PlayGround v-if="$store.state.pk.status === 'playing'">

    </PlayGround>
    <MatchGround v-if="$store.state.pk.status === 'matching'">

    </MatchGround>

</template>

<script>
import PlayGround from "@/components/PlayGround"
import MatchGround from "@/components/MatchGround"
import { onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

export default{
    components: {
        PlayGround,
        MatchGround,
    },
    setup(){
        const store = useStore();
        // 与后端@ServerEndpoint注解中的链接相对应
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`; // websocket has no post method body -- put token in the url

        let socket = null;
        // 组件（当前界面）被加载时执行
        onMounted(() => {
            store.commit("updateOpponent", {
                username: "waiting",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
            })
            socket = new WebSocket(socketUrl);
            // WebSocket自带的函数
            socket.onopen = () => {
                console.log("connected!");
                store.commit("updateSocket", socket);
            }

            socket.onmessage = msg => {
                const data = JSON.parse(msg.data);
                if(data.event === "start-matching"){
                    store.commit("updateOpponent",{
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    })
                    setTimeout(() => {
                        store.commit("updateStatus","playing");
                    },2000)
                    store.commit("updateGamemap", data.game);
                }
            }

            socket.onclose = () => {
                console.log("disconnected");
            }
        })

        // 组件被挂载时执行
        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus","matching");
        })
    }
}
</script>

<style scoped>
    
</style>
