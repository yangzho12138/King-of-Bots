<template>
   <PlayGround v-if="$store.state.pk.status === 'playing'">

    </PlayGround>
    <MatchGround v-if="$store.state.pk.status === 'matching'">

    </MatchGround>
    <ResultBoard v-if="$store.state.pk.loser !== 'none'">
    </ResultBoard>

</template>

<script>
import PlayGround from "@/components/PlayGround"
import MatchGround from "@/components/MatchGround"
import ResultBoard from "@/components/ResultBoard"
import { onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'

export default{
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup(){
        const store = useStore();
        // 与后端@ServerEndpoint注解中的链接相对应
        const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}`; // websocket has no post method body -- put token in the url
        
        store.commit("updateLoser", "none");
        store.commit("updateIsRecord", false);

        let socket = null;
        // 组件（当前界面）被加载时执行
        onMounted(() => {
            store.commit("updateIsRecord", false);
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
                        store.commit("updateStatus","playing"); // 匹配成功后等待2s开始游戏
                    },2000)
                    store.commit("updateGamemap", data.game);
                }else if(data.event === "move"){
                    const game = store.state.pk.gameObject;
                    const[snake0, snake1] = game.snakes;
                    //console.log(snake0.status);
                    if(data.a_direction >= 0 && snake0.status !== "die"){ 
                        //console.log(data, snake0.status);
                        snake0.set_direction(data.a_direction);
                    }
                    if(data.b_direction >= 0 && snake1.status !== "die"){
                        //console.log(data, snake1.status);
                        snake1.set_direction(data.b_direction);
                    }
                }else if(data.event === "result"){
                    //console.log(data);
                    const game = store.state.pk.gameObject;
                    const[snake0, snake1] = game.snakes;
                    if(data.loser === "all" || data.loser === "A")
                        snake0.status = "die";
                    if(data.loser === "all" || data.loser === "B")
                        snake1.status = "die";
                    store.commit("updateLoser", data.loser);
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
