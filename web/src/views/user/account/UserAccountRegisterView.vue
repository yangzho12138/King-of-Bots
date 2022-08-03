<template>
    <ContentField v-if="!$store.state.user.register_info">
        <div class="row justify-content-md-center">
            <div class="col-6">
                <form @submit.prevent="register"> 
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <div class="input-group has-validation">
                            <span class="input-group-text" id="inputGroupPrepend">@</span>
                            <input v-model="username" type="email" class="form-control" id="username" placeholder="Please enter your email address as your username">
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="Please enter the password">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">Confirmed Password</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="Please enter the password again">
                    </div>
                    <form class="row g-6">
                        <div class="col-9 mb-3">
                            <label for="verificationCode" class="form-label">Verification Code</label>
                            <input v-model="verificationCode" type="text" class="form-control" id="verificationCode" placeholder="Please enter the verification code">
                        </div>
                        <div class="col-3 mb-3">
                            <button class="send btn btn-primary" type="submit">
                                Send Code
                            </button>
                        </div>
                    </form>
                    <div class="error-message mb-3">{{error_message}}</div>
                    <button type="submit" class="submit btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </ContentField>
    <ContentField v-if="$store.state.user.register_info"> 
        <div>
            Register Success! Enjoy your time!
        </div>
        <router-link @click="restoreRegisterPage" role="button" :to="{name: 'user_account_login'}">
            Click here to login
        </router-link>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField"
import { ref } from 'vue'
import $ from "jquery"
import { useStore } from "vuex"

const TIME_COUNT = 60;
export default{
    components: {
        ContentField,
    },

    // Vue加载页面自动触发函数，开始监听发送验证码的按钮
    mounted: function(){
        this.getCodeAgain();
    },

    setup(){
        let username = ref("");
        let password = ref("");
        let confirmedPassword = ref("");
        let error_message = ref("");

        const store = useStore();

        let verificationCode = ref("");

        // 注册不会修改state的值，因此不用像login那样把ajax的函数放到store里去
        const register = () =>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                    verificationCode: verificationCode.value,
                },
                success(resp){
                    if(resp.message === "success"){
                        store.commit("updateRegisterInfo",true);
                    }
                    else
                        error_message.value = resp.message;
                },
                error(resp){
                    error_message.value = resp.message;
                }
            })
        }

        // get Verification Code
        const getVC = () =>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/account/register/getVC/",
                type: "post",
                data: {
                    username: username.value,
                },
                success(resp){
                    console.log(resp);
                    if(resp.message !== "success")
                        error_message.value = resp.message;
                },
                error(resp){
                    console.log(resp);
                    error_message.value = resp.message;
                }
            })
        }


        const getCodeAgain = () => {
            var btn = document.querySelector(".send"); // 写在函数外面无法监听.send类
            var time = TIME_COUNT;
            btn.addEventListener("click",function(){
                getVC();
                btn.disabled = true;
                var timer = setInterval(function(){
                    if(time === 0){
                        clearInterval(timer);
                        btn.disabled = false;
                        btn.innerHTML = "Send Code";
                    }else{
                        btn.innerHTML = time + " s";
                        time--;
                    }
                },1000);
            });
        }

        const restoreRegisterPage = () => {
            store.commit("updateRegisterInfo",false);
        }

        return{
            username,
            password,
            confirmedPassword,
            error_message,
            verificationCode,
            register,
            getVC,
            getCodeAgain,
            restoreRegisterPage,
        }

    },

}


</script>

<style scoped>
    button.submit{
        width: 100%;
    }

    button.send{
        margin-top: 2vw;
    }

    div.error-message{
        color: red;
    }
</style>