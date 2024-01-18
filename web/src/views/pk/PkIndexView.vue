<template>
  <PlayGround v-if="$store.state.pk.status === 'playing'" />
  <MatchGround v-if="$store.state.pk.status === 'matching'" />
  <ResultBoard v-if="$store.state.pk.winner !== 'none'" />
  <div class="user-color" v-if="$store.state.pk.status === 'playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.a_id)">您在左下角（WASD或↑ ↓ ← → 控制方向）</div>
  <div class="user-color" v-if="$store.state.pk.status === 'playing' && parseInt($store.state.user.id) === parseInt($store.state.pk.b_id)">您在右上角（WASD或↑ ↓ ← → 控制方向）</div>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import ResultBoard from "@/components/ResultBoard.vue";

export default {
  components: {
    ResultBoard,
    PlayGround,
    MatchGround,
  },
  setup() {
    const store = useStore();

    store.commit("updateWinner", "none");
    store.commit("updateIsRecord", false);

    const socketUrl = `wss://app5163.acapp.acwing.com.cn/websocket/${store.state.user.token}/`;

    let socket = null;
    onMounted(() => {
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      })
      socket = new WebSocket(socketUrl);

      socket.onopen = () => {
        console.log("connected!");
        store.commit("updateSocket", socket);
      }

      socket.onmessage = msg => {
        const data = JSON.parse(msg.data);
        if (data.event === "start-matching") {  // 匹配成功
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          setTimeout(() => {
            store.commit("updateStatus", "playing");
          }, 2000);
          store.commit("updateGame", data.game);
        } else if (data.event === "move") {
          // console.log("data为: ",data)
          const game = store.state.pk.gameObject;
          // console.log("game对象为：", game)
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);
        } else if (data.event === "result") {
          // console.log(data)
          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;
          if (data.winner === "all" || data.winner === "B") {
            snake0.status = "die";
          }
          if (data.winner === "all" || data.winner === "A") {
            snake1.status = "die";
          }
          store.commit("updateWinner",data.winner);
        }
      }

      socket.onclose = () => {
        console.log("disconnected!");
      }
    });

    onUnmounted(() => {
      socket.close();
      store.commit("updateStatus", "matching");
    })
  }
}
</script>

<style scoped>
div.user-color {
  text-align: center;
  color: white;
  font-size: 30px;
  font-weight: 600;
}
</style>
