<template>
  <header class="topbar">
    <div>
      <p class="topbar__event">{{ eventName }}</p>
      <h1 class="topbar__title">{{ dateLabel }}</h1>
    </div>
    <div class="topbar__guest">
      <span class="topbar__badge">{{ guestName }}</span>
      <button type="button" :disabled="disabled" @click="$emit('login')">ゲストでログイン</button>
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps<{
  eventName: string;
  dateLabel: string;
  guestName: string;
  disabled?: boolean;
}>();

defineEmits<{
  login: [];
}>();
</script>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid #cde8dd;
  background: linear-gradient(145deg, #effff7, #f7fffb);
}

.topbar__event {
  margin: 0;
  font-size: 12px;
  color: #3e7360;
}

.topbar__title {
  margin: 2px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: #173a2c;
}

.topbar__guest {
  display: flex;
  align-items: center;
  gap: 10px;
}

.topbar__badge {
  padding: 6px 10px;
  border-radius: 999px;
  background: #163f30;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

.topbar button {
  height: 34px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  background: #2b7a58;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

.topbar button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
