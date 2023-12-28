<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card" style="margin-top: 20px;">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="" style="width: 100%;">
          </div>
        </div>
      </div>
      <div class="col-9">
        <div class="card" style="margin-top: 20px;">
          <div class="card-header">
            <span style="font-size: 130%">我的Bot</span>
            <button type="button" class="btn btn-success float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
              创建Bot
            </button>

            <!-- Modal -->
            <div class="modal fade" id="add-bot-btn" tabindex="-1">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h5 class="modal-title">创建Bot</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-title" class="form-label">名称</label>
                      <input v-model="botAdd.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-description" class="form-label">简介</label>
                      <textarea v-model="botAdd.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <VAceEditor
                          v-model:value="botAdd.content"
                          @init="editorInit"
                          lang="c_cpp"
                          theme="textmate"
                          style="height: 300px"
                          :options="{
                            enableBasicAutocompletion: true, // 启用基本自动完成
                            enableSnippets: true, // 启用代码段
                            enableLiveAutocompletion: true, // 启用实时自动完成
                            fontSize: 16, // 设置字号
                            tabSize: 2, // 标签大小
                            showPrintMargin: false, // 去除编辑器里的竖线
                            highlightActiveLine: true,
                          }"/>
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div class="error-message">{{ botAdd.error_message }}</div>
                    <button type="button" class="btn btn-success" @click="add_bot">创建</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-body">
            <table class="table table-striped table-hover">
              <thead>
              <tr>
                <th>名称</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="bot in bots" :key="bot.id">
                <td>{{ bot.title }}</td>
                <td>{{ bot.gmtCreate }}</td>
                <td>
                  <button type="button" class="btn btn-primary" style="margin-right: 10px;" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-' + bot.id">修改</button>
                  <button type="button" class="btn btn-danger" data-bs-toggle="modal" :data-bs-target="'#delete-bot-modal-' + bot.id">删除</button>

                  <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                    <div class="modal-dialog modal-xl">
                      <div class="modal-content">
                        <div class="modal-header">
                          <h5 class="modal-title">修改Bot</h5>
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                          <div class="mb-3">
                            <label for="add-bot-title" class="form-label">名称</label>
                            <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                          </div>
                          <div class="mb-3">
                            <label for="add-bot-description" class="form-label">简介</label>
                            <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                          </div>
                          <div class="mb-3">
                            <label for="add-bot-code" class="form-label">代码</label>
                            <VAceEditor
                                v-model:value="bot.content"
                                @init="editorInit"
                                lang="c_cpp"
                                theme="textmate"
                                style="height: 300px"
                                :options="{
                                  enableBasicAutocompletion: true, // 启用基本自动完成
                                  enableSnippets: true, // 启用代码段
                                  enableLiveAutocompletion: true, // 启用实时自动完成
                                  fontSize: 16, // 设置字号
                                  tabSize: 2, // 标签大小
                                  showPrintMargin: false, // 去除编辑器里的竖线
                                  highlightActiveLine: true,
                                }"/>
                          </div>
                        </div>
                        <div class="modal-footer">
                          <div class="error-message">{{ botUpdate.error_message }}</div>
                          <button type="button" class="btn btn-success" @click="update_bot(bot)">保存修改</button>
                          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="modal" :id="'delete-bot-modal-' + bot.id" tabindex="-1" style="">
                    <div class="modal-dialog">
                      <div class="modal-content">
                        <div class="modal-header">
                          <h5 class="modal-title">确认删除</h5>
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                          <p>请确认是否删除该Bot!</p>
                        </div>
                        <div class="modal-footer">
                          <button type="button" class="btn btn-danger" @click="remove_bot(bot)">确认删除</button>
                          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                        </div>
                      </div>
                    </div>
                  </div>

                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from "vue3-ace-editor";
import ace from "ace-builds";

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';
import 'ace-builds/src-noconflict/mode-c_cpp';

export default {
  components: {
    VAceEditor
  },
  setup() {
    ace.config.set(
        "basePath",
        "https://cdn.jsdelivr.net/npm/ace-builds@" +
        require("ace-builds").version +
        "/src-noconflict/")

    const store = useStore();
    let bots = ref([]);

    const botAdd = reactive({
      title: "",
      description: "",
      content: "",
      error_message: "",
    });

    const botUpdate = reactive({
      error_message: "",
    });

    const refresh_bots = () => {
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/getList/",
        type: "get",
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          bots.value = resp;
        }
      })
    }

    refresh_bots();

    const add_bot = () => {
      botAdd.error_message = "";
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/add/",
        type: "post",
        data: {
          title: botAdd.title,
          description: botAdd.description,
          content: botAdd.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            botAdd.title = "";
            botAdd.description = "";
            botAdd.content = "";
            Modal.getInstance("#add-bot-btn").hide();
            refresh_bots();
          } else {
            botAdd.error_message = resp.error_message;
          }
        }
      })
    }

    const update_bot = (bot) => {
      botUpdate.error_message = "";
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/update/",
        type: "post",
        data: {
          botId: bot.id,
          title: bot.title,
          description: bot.description,
          content: bot.content,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            Modal.getInstance('#update-bot-modal-' + bot.id).hide();
            refresh_bots();
          } else {
            botUpdate.error_message = resp.error_message;
          }
        }
      })
    }

    const remove_bot = (bot) => {
      $.ajax({
        url: "http://127.0.0.1:3000/user/bot/delete/",
        type: "post",
        data: {
          botId: bot.id,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          if (resp.error_message === "success") {
            Modal.getInstance('#delete-bot-modal-' + bot.id).hide();
            refresh_bots();
          }
        }
      })
    }

    return {
      bots,
      botAdd,
      botUpdate,
      add_bot,
      update_bot,
      remove_bot,
    }
  }
}
</script>

<style scoped>
div.error-message {
  color: red;
}
</style>
