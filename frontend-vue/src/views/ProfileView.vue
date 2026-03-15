<template>
  <section class="card">
    <h2>个人中心</h2>
    <div class="row">
      <div class="tag">当前用户：{{ session.username }} ({{ session.userId }})</div>
      <div class="tag">秒杀结果：{{ seckillResult || "-" }}</div>
    </div>
    <div class="status">我的订单</div>
    <div class="grid">
      <div v-for="order in orders" :key="order.id" class="product-card">
        <div>订单号：{{ order.id }}</div>
        <div>商品：{{ order.productId }}</div>
        <div>状态：{{ order.status }}</div>
        <div>时间：{{ order.createdAt }}</div>
      </div>
      <div v-if="orders.length === 0" class="muted">暂无订单</div>
    </div>
  </section>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { session, seckillResult } from "../store/session";

const orders = ref([]);

const loadOrders = async () => {
  if (!session.value.userId) {
    orders.value = [];
    return;
  }
  const response = await fetch(`/api/orders?userId=${session.value.userId}`);
  if (!response.ok) {
    orders.value = [];
    return;
  }
  orders.value = await response.json();
};

onMounted(loadOrders);
</script>
