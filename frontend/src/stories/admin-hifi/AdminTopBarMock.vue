<template>
  <header class="topbar">
    <div>
      <p class="topbar__event">{{ eventName }}</p>
      <h2 class="topbar__title">{{ sectionLabel }}</h2>
    </div>
    <div class="topbar__meta">
      <span class="topbar__badge">{{ adminName }}</span>
      <button type="button">Sign out</button>
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps<{
  eventName: string;
  sectionLabel: string;
  adminName: string;
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
  border: 1px solid #f2cf87;
  background: linear-gradient(145deg, #fff7df, #fffcf2);
  font-family: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
}

.topbar__event {
  margin: 0;
  font-size: 12px;
  color: #6b4b0f;
}

.topbar__title {
  margin: 2px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: #4a2f06;
}

.topbar__meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.topbar__badge {
  padding: 6px 10px;
  border-radius: 999px;
  background: #5b3a09;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.topbar button {
  height: 34px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  background: #b45309;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}
</style>
