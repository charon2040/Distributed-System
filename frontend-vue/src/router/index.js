import { createRouter, createWebHistory } from "vue-router";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import ProductsView from "../views/ProductsView.vue";
import ProfileView from "../views/ProfileView.vue";
import { session } from "../store/session";

// 前端路由表
const routes = [
  { path: "/", redirect: "/products" },
  { path: "/login", component: LoginView },
  { path: "/register", component: RegisterView },
  { path: "/products", component: ProductsView },
  { path: "/profile", component: ProfileView, meta: { requiresAuth: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局守卫：个人中心需登录
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !session.value.userId) {
    next("/login");
    return;
  }
  next();
});

export default router;
