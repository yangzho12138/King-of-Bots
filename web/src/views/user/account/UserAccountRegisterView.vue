<template>
    <ContentField v-if="!$store.state.user.register_info">
        <div class="row justify-content-md-center">
            <div class="col-6">
                <form @submit.prevent="register"> 
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input v-model="username" type="email" class="form-control" id="username" placeholder="Please enter the username">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="Please enter the password">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">Confirmed Password</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="Please enter the password again">
                    </div>
                    <div class="error-message mb-3">{{error_message}}</div>
                    <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </ContentField>
    <ContentField v-if="$store.state.user.register_info"> 
        <div>
            Register Success! Enjoy your time!
        </div>
        <router-link role="button" :to="{name: 'user_account_login'}">
            Click here to login
        </router-link>
    </ContentField>
</template>

<script>
import ContentField from "@/components/ContentField"
import { ref } from 'vue'
import $ from "jquery"
import { useStore } from "vuex"

export default{
    components: {
        ContentField,
    },

    setup(){
        let username = ref("");
        let password = ref("");
        let confirmedPassword = ref("");
        let error_message = ref("");

        const store = useStore();

        // 注册不会修改state的值，因此不用像login那样把ajax的函数放到store里去
        const register = () =>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
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

        return{
            username,
            password,
            confirmedPassword,
            error_message,
            register,
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