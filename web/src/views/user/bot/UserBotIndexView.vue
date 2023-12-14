<template>
  <ContentField>
    我的Bot
  </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'
import {useStore} from "vuex";

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    // $.ajax({
    //   url: "http://127.0.0.1:8090/user/bot/add/",
    //   type: "post",
    //   data: {
    //     title: "Bot标题222",
    //     description: "Bot描述",
    //     content: "111",
    //   },
    //   headers: {
    //     Authorization: "Bearer " + store.state.user.token,
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp)
    //   }
    // })

    // $.ajax({
    //   url: "http://127.0.0.1:8090/user/bot/delete/",
    //   type: "post",
    //   data: {
    //     botId: "3"
    //   },
    //   headers: {
    //     Authorization: "Bearer " + store.state.user.token,
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp)
    //   }
    // })

    // $.ajax({
    //   url: "http://127.0.0.1:8090/user/bot/update/",
    //   type: "post",
    //   data: {
    //     botId: "4",
    //     title: "Bot标题aa",
    //     description: "Bot描述aa",
    //     content: "111a",
    //   },
    //   headers: {
    //     Authorization: "Bearer " + store.state.user.token,
    //   },
    //   success(resp) {
    //     console.log(resp);
    //   },
    //   error(resp) {
    //     console.log(resp)
    //   }
    // })

    $.ajax({
      url: "http://127.0.0.1:8090/user/bot/getList/",
      type: "get",
      headers: {
        Authorization: "Bearer " + store.state.user.token,
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp)
      }
    })
  }
}
</script>

<style scoped>
</style>
