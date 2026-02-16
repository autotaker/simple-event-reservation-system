<template>
  <section class="table-wrap">
    <header class="table-wrap__header">
      <h3>管理対象セッション</h3>
      <span>{{ sessions.length }}件</span>
    </header>

    <ul>
      <li v-for="session in sessions" :key="session.id">
        <div>
          <p class="meta">{{ session.slot }} | {{ session.track }}</p>
          <p class="title">{{ session.title }}</p>
        </div>
        <button type="button" :disabled="disabled">編集</button>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  sessions: { id: string; slot: string; track: string; title: string }[];
  disabled?: boolean;
}>();
</script>

<style scoped>
.table-wrap {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #efd59b;
  background: #fffef8;
}

.table-wrap__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-wrap__header h3,
.table-wrap__header span {
  margin: 0;
}

.table-wrap ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

.table-wrap li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 10px;
  border: 1px solid #f3e0b7;
  border-radius: 10px;
  background: #fff;
}

.meta,
.title {
  margin: 0;
}

.meta {
  font-size: 12px;
  color: #7a5a24;
}

.title {
  font-size: 14px;
  color: #3d2a06;
}

.table-wrap button {
  height: 30px;
  border: none;
  border-radius: 9px;
  padding: 0 12px;
  background: #9a3412;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.table-wrap button:disabled {
  background: #c3ab84;
  cursor: not-allowed;
}
</style>
