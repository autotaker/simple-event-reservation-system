<template>
  <section class="reservation-panel">
    <header>
      <h4>予約一覧</h4>
      <button type="button" :disabled="disabled">更新</button>
    </header>
    <ul>
      <li v-for="item in reservations" :key="item.id">
        <p>{{ item.title }}</p>
        <span>{{ item.time }}</span>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  reservations: { id: string; title: string; time: string }[];
  disabled?: boolean;
}>();
</script>

<style scoped>
.reservation-panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #d9e8f4;
  background: #ffffff;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h4 {
  margin: 0;
  font-size: 14px;
}

button {
  height: 30px;
  padding: 0 10px;
  border: none;
  border-radius: 8px;
  background: #315e96;
  color: #ffffff;
  font-size: 12px;
}

button:disabled {
  opacity: 0.6;
}

ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

li {
  display: grid;
  gap: 2px;
  padding: 8px;
  border-radius: 10px;
  background: #f8fbff;
}

p,
span {
  margin: 0;
}

p {
  font-size: 13px;
}

span {
  font-size: 11px;
  color: #4f657f;
}
</style>
