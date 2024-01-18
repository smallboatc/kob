<template>
  <div class="content-field">
    <slot></slot>
    <div class="go-back" @click="click_go_back_handler">
      返回
    </div>
  </div>
</template>

<script>
import { useStore } from 'vuex';

export default {
  setup() {
    const store = useStore();

    const click_go_back_handler = () => {
      store.commit("updateRouterName", "menu");
    }

    return {
      click_go_back_handler,
    }
  }
}
</script>

<style scoped>
div.content-field {
  width: 100%;
  height: 100%;
}
div.go-back {
  position:absolute;
  right: 5vh;
  bottom: 5vh;
  color: white;
  font-size: 24px;
  font-style: italic;
  font-weight: 600;
  cursor: pointer;
  user-select: none;
}
div.go-back:hover {
  scale: 1.2;
  transition: 200ms;
}
</style>
