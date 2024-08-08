<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand" style="color: red" :to="{name: 'home'}">King of Bots</router-link>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
          <li class="nav-item">
            <router-link :class="route_name === 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">对战</router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">对局列表</router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">排行榜</router-link>
          </li>
          <li class="nav-item">
            <router-link :class="route_name === 'info_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'info_index'}">游戏规则与Bot样例</router-link>
          </li>
        </ul>
        <ul class="navbar-nav" v-if="$store.state.user.is_login">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              {{ $store.state.user.username }}
            </a>
            <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
              <li>
                <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">个人主页</router-link>
              </li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
            </ul>
          </li>
        </ul>
        <!--<ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">-->
        <ul class="navbar-nav" v-else>
          <li class="nav-item dropdown">
            <router-link class="nav-link" :to="{name: 'user_account_login'}" role="button">
              登录
            </router-link>
          </li>
          <li class="nav-item dropdown">
            <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
              注册
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
import { useStore } from 'vuex';

export default {
  setup() {
    const store = useStore();
    const route = useRoute();
    let route_name = computed(() => route.name)

    const logout = () => {
      store.dispatch("logout");
    }

    return {
      route_name,
      logout
    }
  }
}
</script>

<style scoped>

</style>
