import { ref } from "vue";

const storedUser = JSON.parse(localStorage.getItem("sessionUser") || "null");
const storedSeckill = localStorage.getItem("seckillResult") || "";

export const session = ref({
  userId: storedUser?.id || null,
  username: storedUser?.username || null
});

export const seckillResult = ref(storedSeckill);

export const setSession = (user) => {
  session.value = { userId: user.id, username: user.username };
  localStorage.setItem("sessionUser", JSON.stringify(user));
};

export const clearSession = () => {
  session.value = { userId: null, username: null };
  localStorage.removeItem("sessionUser");
};

export const setSeckillResult = (value) => {
  seckillResult.value = value;
  localStorage.setItem("seckillResult", value);
};
