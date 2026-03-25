<template>
  <section class="card">
    <h2>注册</h2>
    <div class="row">
      <div>
        <div class="muted">用户名</div>
        <input v-model="form.username" placeholder="用户名" />
      </div>
      <div>
        <div class="muted">密码</div>
        <input v-model="form.password" type="password" placeholder="密码" />
      </div>
      <div>
        <div class="muted">&nbsp;</div>
        <button class="btn" @click="register">注册</button>
      </div>
      <div class="tag">实例: {{ instanceName }}</div>
    </div>
    <div class="status">{{ message }}</div>
    <div class="row">
      <span class="muted">已有账号？</span>
      <router-link class="btn btn-outline" to="/login">去登录</router-link>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { setSession } from "../store/session";

// 注册页状态
const router = useRouter();

const form = reactive({
  username: "",
  password: ""
});

const message = ref("");
const instanceName = ref("-");

// 调用后端注册接口
const register = async () => {
  message.value = "";
  const response = await fetch("/api/users/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      username: form.username.trim(),
      password: form.password
    })
  });
  instanceName.value = response.headers.get("X-Instance") || "-";
  const data = await response.json();
  if (response.ok) {
    setSession({ id: data.id, username: data.username });
    router.push("/products");
    return;
  }
  message.value = `注册失败: ${response.status} ${data.message || ""}`;
};
</script>
