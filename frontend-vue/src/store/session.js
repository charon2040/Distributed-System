import { ref } from "vue";

// 启动时恢复本地会话与秒杀结果
const storedUser = JSON.parse(localStorage.getItem("sessionUser") || "null");
const storedSeckill = localStorage.getItem("seckillResult") || "";

export const session = ref({
  userId: storedUser?.id || null,
  username: storedUser?.username || null
});

export const seckillResult = ref(storedSeckill);

// 写入登录态
export const setSession = (user) => {
  session.value = { userId: user.id, username: user.username };
  localStorage.setItem("sessionUser", JSON.stringify(user));
};

// 清空登录态
export const clearSession = () => {
  session.value = { userId: null, username: null };
  localStorage.removeItem("sessionUser");
};

// 写入秒杀提示
export const setSeckillResult = (value) => {
  seckillResult.value = value;
  localStorage.setItem("seckillResult", value);
};
