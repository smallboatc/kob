<template>
  <div class="content-field">
    <div class="playground">
      <GameMap />
    </div>
    <div class="go-back" @click="click_go_back_handler">
      返回
    </div>
  </div>
</template>

<script>
import GameMap from './GameMap.vue'
import { useStore } from 'vuex';

export default {
  components: {
    GameMap,
  },
  setup() {
    const store = useStore();

    const click_go_back_handler = () => {
      store.commit("updateRouterName", "record");
    }

    return {
      click_go_back_handler,
    }
  }
}
</script>

<style scoped>
div.content-field {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}
div.playground {
  width: 40vw;
  height: 50vh;
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
  transform: scale(1.2);
  transition: 200ms;
}
</style>
