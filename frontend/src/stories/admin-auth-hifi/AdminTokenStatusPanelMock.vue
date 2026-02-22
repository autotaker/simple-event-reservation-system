<template>
  <section class="panel" :class="{ 'panel--disabled': disabled }">
    <h3>認証状態</h3>
    <ul>
      <li>operatorId: {{ operatorId }}</li>
      <li>有効期限（固定30分）: {{ expiresAt }}</li>
      <li>保存先: sessionStorage</li>
      <li>管理API: {{ apiStatus }}</li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  operatorId: string;
  expiresAt: string;
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

.panel--disabled {
  opacity: 0.7;
}
</style>
