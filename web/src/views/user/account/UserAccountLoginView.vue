<template>
    <ContentField v-if="!$store.state.user.pulling_info">
        <div class="row justify-content-md-center">
            <div class="col-6">
                <!-- submit按钮触发login函数 -->
                <form @submit.prevent="login"> 
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <!-- v-model将输入绑定变量名 -->
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="Please enter the username">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="Please enter the password">
                    </div>
                    <div class="error-message mb-3">{{error_message}}</div>
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>
    
<script>
import ContentField from "@/components/ContentField"
import { useStore } from "vuex"
import { ref } from "vue"
import router from "@/router/index"

export default{
    components: {
        ContentField
    },

    setup(){
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        // 刷新之后-进入未登陆状态，未授权的页面（router的index.js中）会默认跳转到登录页面
        // 进入登录页面前检查本地是否有token，使其保有登陆状态
        const jwt_token = localStorage.getItem("jwt_token");
        if(jwt_token){
            store.commit("updateToken", jwt_token); // 调用store的mutation中的函数
            // 验证token
            store.dispatch("getinfo",{
                success(){
                    router.push({name: "home"})
                    store.commit("updatePullingInfo",false);
                },
                error(){
                    // token过期
                    store.commit("updatePullingInfo",false);
                },
            })
        }else{
            store.commit("updatePullingInfo",false);
        }


        const login = () => {
            error_message.value = "";
            store.dispatch("login",{ // 调用store的actions中的login函数
                username: username.value, // ref的取值要加.value
                password: password.value,
                success() {
                    store.dispatch("getinfo",{
                        success(){
                            router.push({name: 'home'});
                        },

                    })
                    router.push({name: 'home'});
                },
                error(){
                    error_message.value = "the username or password is wrong";
                }
            })
        }

        return{
            username,
            password,
            error_message,
            login,
        }
    }
}
</script>

<style scoped>
    button{
        width: 100%;
    }

    div.error-message{
        color: red;
    }
</style>