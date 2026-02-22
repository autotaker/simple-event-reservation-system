<template>
  <section class="panel" :class="{ 'panel--disabled': disabled }">
    <h3>ログアウト（当該トークン失効）</h3>
    <p class="panel__desc">
      `POST /api/auth/admin/logout` を実行し、現在のアクセストークンのみを失効します。
    </p>
    <button type="button" :disabled="disabled">ログアウトして失効する</button>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  disabled?: boolean;
}>();
</script>

<style scoped>
.panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-danger);
  background: var(--semantic-color-bg-surface);
}

.panel h3,
.panel__desc {
  margin: 0;
}

.panel__desc {
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.panel button {
  width: fit-content;
  min-width: 180px;
  height: 34px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  background: var(--semantic-color-state-danger);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}

.panel--disabled {
  opacity: 0.7;
}
</style>
