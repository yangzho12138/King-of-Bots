<template>
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <div class="container">
            <!-- router-link: the link page will not be refreshed every time (like Link tag in react) -->
            <router-link class="navbar-brand" :to="{name: 'home'}">King of Bots</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" aria-current="page" :to="{name: 'pk_index'}">Fight</router-link>
                </li>
                <li class="nav-item">
                <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">Match List</router-link>
                </li>
                <li class="nav-item">
                <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">Ranking</router-link>
                </li>
            </ul>
            <ul class="navbar-nav" v-if="$store.state.user.is_login">
                <div class="dropdown">
                    <button class="btn dropdown-toggle" type="button" id="dropdownMenuButton2" data-bs-toggle="dropdown" aria-expanded="false">
                        {{ $store.state.user.username }}
                    </button>
                    <ul class="dropdown-menu dropdown-menu-light" aria-labelledby="dropdownMenuButton2">
                        <li><router-link class="dropdown-item" :to="{name: 'user_bot_index'}">My Bot</router-link></li>
                        <li><hr class="dropdown-divider"></li>
                        <!-- 点击Logout按钮调用setup()中的logout函数 -->
                        <li><router-link class="dropdown-item" :to="{name: 'home'}" @click="logout">Logout</router-link></li> 
                    </ul>
                </div>
            </ul>
            <ul class="navbar-nav" v-else>
                <li class="nav-item">
                    <router-link class="nav-link" role="button" :to="{name: 'user_account_login'}">
                        Login
                    </router-link>
                </li>
                <li class="nav-item">
                    <router-link href="#" class="nav-link" role="button" :to="{name: 'user_account_register'}">
                        Register
                    </router-link>
                </li>
            </ul>
            </div>
        </div>
</nav>
</template>

<script>
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { useStore} from 'vuex'

export default {
    setup() {
        // Get the current routing path name and highlight the corresponding button
        const route = useRoute();
        let route_name = computed(() => route.name)

        const store = useStore();
        const logout = () => {
            store.dispatch("logout");
        }

        return{
            route_name,
            logout,
        }
    }
}
</script>

<style scoped>

</style>