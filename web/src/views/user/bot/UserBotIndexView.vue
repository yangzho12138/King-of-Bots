<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px">
                    <div class="card-header">
                        <span style="font-size: 150%"> My Bots </span>
                        <!-- float-end类表示向右对齐 -->
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add_bot_btn"> 
                            Create Bots
                        </button>
                        <!-- Modal: div id对应button标签的data-bs-target 绑定-->
                        <div class="modal fade" id="add_bot_btn" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title">Create Bots</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <form>
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">Title</label>
                                            <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="Please enter the name of bot">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">Description</label>
                                            <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="Please enter the introduction of bot"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label">Code</label>
                                            <VAceEditor
                                                v-model:value="botadd.content"
                                                @init="editorInit"
                                                lang="c_cpp"
                                                theme="textmate"
                                                style="height: 300px" />
                                        </div>
                                    </form>
                                </div>
                                <div class="modal-footer">
                                    <div class="error_message" style="color: red"> {{botadd.error_message}} </div>
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancle</button>
                                    <button @click="add_bot" type="button" class="btn btn-primary">Create</button>
                                </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>Name</th>
                                    <th>Create Time</th>
                                    <th>Operations</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- 循环bot对象 -->
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td> {{bot.title}} </td>
                                    <td> {{bot.createtime}} </td>
                                    <td>
                                        <button type="button" class="btn btn-success" style="margin-right: 10px" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">Modify</button>
                                        <button @click="remove_bot(bot)" type="button" class="btn btn-danger">Delete</button>
                                        <!-- Modal 每一个bot记录有一个单独的浮窗，将bot.id加入到浮窗id中使每一个浮窗的id都不同，方便绑定-->
                                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                                            <div class="modal-dialog modal-lg">
                                                <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">Modify Bots</h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <form>
                                                        <div class="mb-3">
                                                            <label for="add-bot-title" class="form-label">Title</label>
                                                            <!-- v-model绑定不仅可以获取输入，还可以绑定已经有值的数据 -->
                                                            <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="Please enter the name of bot">
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="add-bot-description" class="form-label">Description</label>
                                                            <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="Please enter the introduction of bot"></textarea>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="add-bot-code" class="form-label">Code</label>
                                                            <VAceEditor
                                                                v-model:value="bot.content"
                                                                @init="editorInit"
                                                                lang="c_cpp"
                                                                theme="textmate"
                                                                style="height: 300px" />
                                                        </div>
                                                    </form>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class="error_message" style="color: red"> {{bot.error_message}} </div>
                                                    <button @click="update_bot(bot)" type="button" class="btn btn-primary">Modify</button>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancle</button>
                                                </div>
                                                </div>
                                            </div>
                                        </div>

                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery'
import { useStore } from 'vuex'
import { ref, reactive } from 'vue'
import { Modal } from 'bootstrap/dist/js/bootstrap'
// 安装了vue3-ace-editor依赖，创建代码编辑器
import { VAceEditor } from 'vue3-ace-editor'
import ace from 'ace-builds'

export default{
    components: {
        VAceEditor,
    },

    setup(){
        // 代码编辑器的配置
        ace.config.set(
            "basePath", 
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/"
        )

        const store = useStore();
        let bots = ref([]); // 空列表

        // 绑定参数用ref，绑定对象用reactive
        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });

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

        const add_bot = () =>{
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/add/",
                type: "POST",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.message === "success"){
                        botadd.title = "",
                        botadd.description = "",
                        botadd.content = "",
                        // 关闭浮窗
                        Modal.getInstance("#add_bot_btn").hide();
                        //刷新
                        refresh_bots();

                    }
                    else
                        botadd.error_message = resp.message;
                },
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/remove/",
                type: "POST",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.message === "success"){
                        refresh_bots();
                    }
                },
            })
        }

        const update_bot = (bot) =>{
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/update/",
                type: "POST",
                data: {
                    // 绑定的是bot现有的数据
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,

                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.message === "success"){
                        Modal.getInstance("#update-bot-modal-" + bot.id).hide();
                        refresh_bots();
                    }
                },
            })
        }



        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
        }
        
    }
}

</script>

<style scoped>
    
</style>
