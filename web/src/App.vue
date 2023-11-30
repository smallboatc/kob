<template>
  <NavBar />
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
import $ from 'jquery'

export default {
  components: {
    NavBar
  },
  setup() {
    $.ajax({
      url: "http://localhost:8090/user/account/token/",
      type: "post",
      data: {
        username: "cxc",
        password: "123456"
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
      }
    });

    $.ajax({
      url: "http://localhost:8090/user/account/info/",
      type: "get",
      headers: {
        Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0MjZiZmFhNzJlZGU0ZjBkOTg4MTIxN2U4ZTFiOTkxZSIsIn" +
            "N1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTcwMTMzMzA0MCwiZXhwIjoxNzAyNTQyNjQwfQ.dZ1_c14KVlgbGX7qfenmcMDQt0g0pu" +
            "DQXW7mFKXmPZ0"
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
        console.log("错误")
      }
    });
    $.ajax({
      url: "http://localhost:8090/user/account/register/",
      type: "post",
      data: {
        username: "cxc1",
        password: "123456",
        confirmPassword: "1234561"
      },
      success(resp) {
        console.log(resp);
      },
      error(resp) {
        console.log(resp);
      }
    });
  }
}
</script>

<style>
body {
  background-image: url("@/assets/images/background.jpg");
  background-size: cover;
}
</style>
