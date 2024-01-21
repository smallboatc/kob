<template>
  <div></div>
</template>

<script>
import router from "@/router/index";
import {useStore} from "vuex";
import {useRoute} from "vue-router";
import $ from 'jquery'
export default {
  name: "UserAccountQQReceiveCodeView",
  setup() {
    const myRoute = useRoute();
    const store = useStore();
    $.ajax({
      url: "http://127.0.0.1:3000/api/user/account/qq/web/receiveCode/",
      type: "GET",
      data: {
        code: myRoute.query.code,
        state: myRoute.query.state,
      },
      success: resp => {
        if (resp.result === "success") {
          localStorage.setItem("jwt", resp.jwt);
          store.commit("updateToken", resp.jwt);
          router.push({name: "home"});
          store.commit("updatePullingInfo", false);
        } else {
          router.push({name: "user_account_login"});
        }
      }
    })
  }
}
</script>

<style scoped>

</style>