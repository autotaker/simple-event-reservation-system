<template>
  <section class="panel" :class="{ 'panel--disabled': disabled }">
    <h3>トークン状態</h3>
    <ul>
      <li>Access token: 残り {{ ttlLabel }}</li>
      <li>Refresh token: 有効（最終更新 {{ lastRefreshAt }}）</li>
      <li>管理API: {{ apiStatus }}</li>
    </ul>
    <button type="button" :disabled="disabled">期限延長（更新）</button>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  ttlLabel: string;
  lastRefreshAt: string;
  apiStatus: string;
  disabled?: boolean;
}>();
</script>

<style scoped>
.panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-info);
  background: var(--semantic-color-bg-surface);
}

.panel h3,
.panel ul {
  margin: 0;
}

.panel ul {
  padding-left: 18px;
  display: grid;
  gap: 4px;
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.panel button {
  width: fit-content;
  min-width: 150px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--semantic-color-state-info);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}

.panel--disabled {
  opacity: 0.7;
}
</style>
