<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
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