<template>
  <PlayGround />
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import {useStore} from "vuex";
import store from "@/store";

export default {
  components: {
    PlayGround,
  },
  setup() {
    if (!store.state.user.is_login) {
      const store = useStore();
      const jwt = localStorage.getItem("jwt")
      if (jwt) {
        store.commit("updateToken", jwt)
        store.dispatch("getInfo", {
          success() {
            store.commit("updatePullingInfo", false)
          },
          error() {
            store.commit("updatePullingInfo", false)
          }
        })
      } else {
        store.commit("updatePullingInfo", false)
      }
    }
  }
}
</script>

<style scoped>
</style>
