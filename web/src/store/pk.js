// store web-socket related info
// import $ from 'jquery'

export default ({
  state: {
      status: "matching", // matching interface / playing interface
      socket: null,
      opponent_username: "",
      opponent_photo: "",
      gamemap: null,
  },
  getters: {
  },
  mutations: {
      updateSocket(state, socket){
          state.socket = socket;
      },
      updateOpponent(state, opponent){
          state.opponent_username = opponent.username;
          state.opponent_photo = opponent.photo;
      },
      updateStatus(state, status){
          state.status = status;
      },
      updateGamemap(state, gamemap){
          state.gamemap = gamemap;
      }
  },
  actions: {
  },
  modules: {
    
  }
})