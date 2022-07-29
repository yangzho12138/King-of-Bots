import { createRouter, createWebHistory } from 'vue-router'
import HomeView from "@/views/HomeView"
import PkIndexView from "@/views/pk/PkIndexView"
import RanklistIndexView from "@/views/ranklist/RanklistIndexView"
import RecordIndexView from "@/views/record/RecordIndexView"
import UserBotIndexView from "@/views/user/bot/UserBotIndexView"
import NotFound from "@/views/error/NotFound"

const routes = [
  // router
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path:"/pk/",
    name: "pk_index",
    component: PkIndexView,
  },
  {
    path:"/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
  },
  {
    path:"/record/",
    name: "record_index",
    component: RecordIndexView,
  },
  {
    path:"/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
  },
  {
    path:"/404/",
    name: "404",
    component: NotFound,
  },
  // redirection
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
