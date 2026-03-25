<template>
  <div class="app-shell">
    <header class="title">
      <div>
        <h1>库存与秒杀系统</h1>
      </div>
      <div class="nav">
        <router-link class="btn btn-outline" to="/products">商品主页</router-link>
        <router-link class="btn btn-outline" to="/profile">个人中心</router-link>
        <router-link v-if="!session.userId" class="btn btn-outline" to="/login">登录</router-link>
        <router-link v-if="!session.userId" class="btn" to="/register">注册</router-link>
        <button v-if="session.userId" class="btn" @click="logout">退出</button>
      </div>
    </header>
    <main class="page">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { session, clearSession } from "./store/session";
import { useRouter } from "vue-router";

// 应用顶栏登出动作
const router = useRouter();

const logout = () => {
  clearSession();
  router.push("/login");
};
</script>

<style>
body {
  margin: 0;
  background: linear-gradient(180deg, #f4f6fb 0%, #eef1f8 100%);
}
.app-shell {
  max-width: 1100px;
  margin: 0 auto;
  padding: 28px 20px 40px;
  font-family: "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif;
  min-height: 100vh;
  color: #1f1f1f;
}
.page {
  margin-top: 18px;
}
.title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}
.title h1 {
  margin: 0;
  font-size: 26px;
}
.sub {
  color: #6b7280;
  font-size: 13px;
}
.nav {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
.card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  margin-bottom: 16px;
  box-shadow: 0 6px 18px rgba(31, 41, 55, 0.08);
}
.row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  align-items: center;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  margin-top: 16px;
}
.product-card {
  background: #fdfdff;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e6eaf2;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.product-image {
  width: 100%;
  border-radius: 10px;
  overflow: hidden;
  background: #eef2ff;
}
.product-image img {
  width: 100%;
  height: 150px;
  object-fit: cover;
  display: block;
}
.product-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.product-name {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 6px;
}
.price {
  color: #1677ff;
  font-weight: 600;
  margin-top: 4px;
}
.actions {
  margin-top: 10px;
}
.btn {
  padding: 8px 16px;
  border: 0;
  border-radius: 6px;
  background: #1677ff;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  text-decoration: none;
  box-shadow: 0 6px 14px rgba(22, 119, 255, 0.22);
}
.btn:disabled {
  background: #9bbcff;
  cursor: not-allowed;
}
.btn-outline {
  background: #fff;
  color: #1677ff;
  border: 1px solid #1677ff;
}
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  background: #eef2ff;
  color: #3140a8;
  font-size: 12px;
}
input,
select {
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
  min-width: 160px;
}
.status {
  white-space: pre-line;
  font-size: 13px;
  color: #555;
  margin-top: 8px;
}
.pagination {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: flex-end;
  margin-top: 12px;
}
.muted {
  color: #6b7280;
  font-size: 12px;
}
</style>
