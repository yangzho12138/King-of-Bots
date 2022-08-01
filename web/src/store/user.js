// store user-related info
import $ from "jquery"

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true, // 在处理信息的过程中——通过对该参数进行判断，决定是否需要放出页面，借此可以消除刷新的过程中会有一瞬间（云端在拉取数据）跳转到登录页面的闪回问题
    },
    getters: {
    },
    // 同步操作，不需要从云端拉去信息，可以放在mutations中
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
        },
        updatePullingInfo(state, pulling_info){
            state.pulling_info = pulling_info;
        }
    },
    // 异步操作，需要从云端拉去信息，只能放在actions中
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
                        localStorage.setItem("jwt_token", resp.token); // 将token存储到浏览器的内存中
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
            localStorage.removeItem("jwt_token"); // 登出时移除token
            context.commit("logout");
        }
    },
    modules: {
    }
}