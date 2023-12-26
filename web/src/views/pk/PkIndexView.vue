<template>
  <PlayGround v-if="$store.state.pk.status === 'playing'" />
  <MatchGround v-if="$store.state.pk.status === 'matching'" />
  <ResultBoard v-if="$store.state.pk.winner !== 'none'" />
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

    const socketUrl = `ws://127.0.0.1:8090/websocket/${store.state.user.token}/`;

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
          }, 200);
          store.commit("updateGameMap", data.game);
        } else if (data.event === "move") {
          console.log("data为: ",data)
          const game = store.state.pk.gameObject;
          console.log("game对象为：", game)
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);
        } else if (data.event === "result") {
          console.log(data)
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
</style>
