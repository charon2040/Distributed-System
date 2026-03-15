<template>
  <section class="card">
    <div class="row">
      <button class="btn" @click="loadProducts">刷新商品</button>
      <div class="tag">总数: {{ products.length }}</div>
      <div class="muted">每页</div>
      <select v-model.number="pageSize">
        <option :value="4">4</option>
        <option :value="6">6</option>
        <option :value="8">8</option>
      </select>
    </div>
    <div class="grid">
      <div v-for="product in pagedProducts" :key="product.id" class="product-card">
        <div class="product-image">
          <img :src="product.imageUrl" :alt="product.name" />
        </div>
        <div class="product-body">
          <div class="product-name">{{ product.name }}</div>
          <div class="muted">商品编号：{{ product.id }}</div>
          <div class="price">￥{{ product.price }}</div>
        </div>
        <div class="actions">
          <button class="btn" @click="handleSeckill(product.id)">秒杀</button>
        </div>
      </div>
      <div v-if="pagedProducts.length === 0" class="muted">暂无商品</div>
    </div>
    <div class="pagination">
      <button class="btn btn-outline" :disabled="page <= 1" @click="page -= 1">上一页</button>
      <span class="muted">第 {{ page }} / {{ totalPages }} 页</span>
      <button class="btn btn-outline" :disabled="page >= totalPages" @click="page += 1">下一页</button>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { session, setSeckillResult } from "../store/session";

const router = useRouter();
const products = ref([]);
const page = ref(1);
const pageSize = ref(6);

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(products.value.length / pageSize.value));
});

const pagedProducts = computed(() => {
  const start = (page.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return products.value.slice(start, end);
});

watch([products, pageSize], () => {
  if (page.value > totalPages.value) {
    page.value = totalPages.value;
  }
});

const loadProducts = async () => {
  const response = await fetch("/api/products");
  if (!response.ok) {
    products.value = [];
    return;
  }
  const data = await response.json();
  products.value = data || [];
  page.value = 1;
};

const handleSeckill = async (productId) => {
  if (!session.value.userId) {
    setSeckillResult("请先登录");
    router.push("/login");
    return;
  }
  const response = await fetch("/api/seckill", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      userId: session.value.userId,
      productId
    })
  });
  const data = await response.json();
  if (response.ok) {
    setSeckillResult(`秒杀成功，订单号: ${data.orderId}`);
  } else {
    setSeckillResult(`秒杀失败: ${response.status} ${data.message || ""}`);
  }
  router.push("/profile");
};

onMounted(loadProducts);
</script>
