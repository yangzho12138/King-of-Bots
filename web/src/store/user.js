// store user-related info
import $ from "jquery"

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
    },
    getters: {
    },
    mutations: {
        updateUser(state, user){
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },

        updateToken(state, token){
            state.token = token;
        },

        logout(state){
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        }
    },
    actions: {
        login(context, data){
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if(resp.message === "success"){
                        context.commit("updateToken", resp.token); // 调用mutations中的updateToken函数
                        data.success(resp);
                    }else{
                        data.error(resp); // 回调函数
                    }
                },
                error(resp) {
                    data.error(resp);
                }

            })
        },
        getinfo(context, data){
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/info/",
                type: "get",
                headers:{
                    Authorization: "Bearer " + context.state.token, // 取得state中的token，Authorization格式参见后端JwtAuthenticationTokenFilter
                },
                success(resp){
                    if(resp.message === "success"){
                        context.commit("updateUser", {
                            ...resp, // 解构resp
                            is_login: true,
                        });
                        data.success(resp);
                    }else{
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                }
            })
        },
        logout(context){
            context.commit("logout");
        }
    },
    modules: {
    }
}