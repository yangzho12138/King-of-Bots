import { createStore } from 'vuex'
import ModuleUser from './user' // 名字随便起
import ModulePk from './pk' // 名字随便起

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    user: ModuleUser,
    pk: ModulePk,
  }
})
