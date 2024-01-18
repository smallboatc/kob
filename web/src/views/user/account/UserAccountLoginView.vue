<template>
  <ContentField v-if="!$store.state.user.pulling_info">
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="login">
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
          </div>
          <div class="error-message">{{ error_message }}</div>
          <button type="submit" class="btn btn-primary">登录</button>
        </form>

        <div @click="qq_login" style="cursor: pointer; text-align: center; margin-top: 10px; display: none">
          <img height="30"
               src="https://wiki.connect.qq.com/wp-content/uploads/2013/10/03_qq_symbol-1-250x300.png"
               alt="QQ官方图标"/>
          <br>
          <div style="color: #09e309">
            QQ一键登录
          </div>
        </div>
      </div>
    </div>
  </ContentField>

</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import router from '../../../router/index'
import $ from 'jquery'

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let username = ref('');
    let password = ref('');
    let error_message = ref('');

    const jwt = localStorage.getItem("jwt")
    if (jwt) {
      store.commit("updateToken", jwt)
      store.dispatch("getInfo", {
        success() {
          router.push({name: "home"})
          store.commit("updatePullingInfo", false)
        },
        error() {
          store.commit("updatePullingInfo", false)
        }
      })
    } else {
      store.commit("updatePullingInfo", false)
    }

    const login = () => {
      error_message.value = "";
      store.dispatch("login", {
        username: username.value,
        password: password.value,
        success() {
          store.dispatch("getInfo", {
            success() {
              router.push({ name: 'home' });
              // console.log(store.state.user);
            }
          })
        },
        error() {
          error_message.value = "用户名或密码错误！";
        }
      })
    }

    const qq_login = () => {
      $.ajax({
        url: "https://app5163.acapp.acwing.com.cn/api/user/account/qq/web/applyCode/",
        type: "GET",
        success: resp => {
          if (resp.result === "success") {
            window.location.assign(resp.apply_code_url);
          }
        }
      })
    }

    return {
      username,
      password,
      error_message,
      login,
      qq_login
    }
  }
}
</script>

<style scoped>
button {
  width: 100%;
}
div.error-message {
  color: red;
}
</style>
