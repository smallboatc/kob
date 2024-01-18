<template>
  <ContentField>
    <div class="game-table">
      <div>
        <table style="text-align: center;">
          <thead>
          <tr>
            <th>玩家</th>
            <th>天梯分</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="user in users" :key="user.id">
            <td class="game-table-username">
              <img :src="user.photo" alt="" class="record-user-photo">
              &nbsp;
              <span class="record-user-username">{{ user.username }}</span>
            </td>
            <td>{{ user.rating }}</td>
          </tr>
          </tbody>
        </table>
        <nav aria-label="...">
          <ul style="padding: 0;">
            <li class="game-page-item" @click="click_page(-2)">
              <a class="game-page-link" href="#">前一页</a>
            </li>
            <li :class="'game-page-item ' + page.is_active" v-for="page in pages" :key="page.number" @click="click_page(page.number)">
              <a class="game-page-link" href="#">{{ page.number }}</a>
            </li>
            <li class="game-page-item" @click="click_page(-1)">
              <a class="game-page-link" href="#">后一页</a>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from '../../components/ContentField.vue'
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore();
    let users = ref([]);
    let current_page = 1;
    let total_users = 0;
    let pages = ref([]);

    const click_page = page => {
      if (page === -2) page = current_page - 1;
      else if (page === -1) page = current_page + 1;
      let max_pages = parseInt(Math.ceil(total_users / 10));

      if (page >= 1 && page <= max_pages) {
        pull_page(page);
      }
    }

    const udpate_pages = () => {
      let max_pages = parseInt(Math.ceil(total_users / 10));
      let new_pages = [];
      for (let i = current_page - 2; i <= current_page + 2; i ++ ) {
        if (i >= 1 && i <= max_pages) {
          new_pages.push({
            number: i,
            is_active: i === current_page ? "active" : "",
          });
        }
      }
      pages.value = new_pages;
    }

    const pull_page = page => {
      current_page = page;
      $.ajax({
        url: "https://app5163.acapp.acwing.com.cn/api/rankList/getList/",
        data: {
          page,
        },
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          users.value = resp.users;
          console.log(users.value);
          total_users = resp.users_count;
          udpate_pages();
        },
        error(resp) {
          console.log(resp);
        }
      })
    }

    pull_page(current_page);

    return {
      users,
      pages,
      click_page
    }
  }
}
</script>

<style scoped>
img.record-user-photo {
  width: 4vh;
  border-radius: 50%;
}

div.game-table {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}
div.game-table table {
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 5px;
}
.game-table-username {
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 15vw;
}
td {
  width: 15vw;
}
th {
  text-align: center;
}
.game-page-item {
  display: inline-block;
  padding: 8px 12px;
  background-color: white;
  border: 1px solid #dee2e6;
  cursor: pointer;
  user-select: none;
}
.game-page-item:hover {
  background-color: #E9ECEF;
}
.game-page-item.active {
  background-color: #0d6efd;
}
.game-page-item.active > a {
  color: white;
}
.game-page-link {
  color: #0d6efd;
  text-decoration: none;
}
nav {
  display: flex;
  justify-content: center;
  align-items: center;
}

</style>
