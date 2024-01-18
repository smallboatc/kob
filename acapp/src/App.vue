<template>
  <div class="game-body">
    <MenuView v-if="$store.state.router.router_name === 'menu'"/>
    <PkIndexView v-else-if="$store.state.router.router_name === 'pk'"/>
    <RecordIndexView v-else-if="$store.state.router.router_name === 'record'"/>
    <RecordContentView v-else-if="$store.state.router.router_name === 'record_content'"/>
    <RanklistIndexView v-else-if="$store.state.router.router_name === 'ranklist'"/>
    <UserBotIndexView v-else-if="$store.state.router.router_name === 'user_bot'"/>
  </div>
</template>

<script>
import { useStore } from "vuex";
import MenuView from './views/MenuView.vue'
import PkIndexView from "@/views/pk/PkIndexView.vue";
import RecordIndexView from "@/views/record/RecordIndexView.vue";
import RecordContentView from "@/views/record/RecordContentView.vue";
import RanklistIndexView from "@/views/ranklist/RanklistIndexView.vue";
import UserBotIndexView from "@/views/user/bot/UserBotIndexView.vue";
import $ from 'jquery';

export default {
  components: {
    MenuView,
    PkIndexView,
    RecordIndexView,
    RecordContentView,
    RanklistIndexView,
    UserBotIndexView
  },
  setup() {
    const store = useStore();

    $.ajax({
      url: "https://app5163.acapp.acwing.com.cn/api/user/account/acwing/acapp/applyCode/",
      type: "GET",
      success: resp => {
        if (resp.result === "success") {
          store.state.user.AcWingOS.api.oauth2.authorize(resp.appid, resp.redirect_uri, resp.scope, resp.state, resp => {
            if (resp.result === "success") {
              const jwt = resp.jwt;
              store.commit("updateToken", jwt);
              store.dispatch("getInfo", {
                success() {
                  store.commit("updatePullingInfo", false);
                },
                error() {
                  store.commit("updatePullingInfo", false);
                }
              })
            } else {
              store.state.user.AcWingOS.api.window.close();
            }
          });
        } else {
          store.state.user.AcWingOS.api.window.close();
        }
      }
    });
  }
}
</script>

<style scoped>
div.game-body {
  background-image: url("@/assets/images/background.jpg");
  background-size: cover;
  width: 100%;
  height: 100%;
}
</style>
