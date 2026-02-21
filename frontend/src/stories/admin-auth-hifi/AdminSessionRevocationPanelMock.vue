<template>
  <section class="panel" :class="{ 'panel--disabled': disabled }">
    <h3>セッション失効</h3>
    <p class="panel__desc">漏えい疑い時は失効操作を優先し、再ログインを強制します。</p>
    <div class="panel__actions">
      <button type="button" :disabled="disabled">全セッションを失効</button>
      <button type="button" :disabled="disabled">この端末のみ失効</button>
    </div>
    <label>
      監査メモ
      <textarea :disabled="disabled">疑わしいIPアクセスを検知したため失効</textarea>
    </label>
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

.panel__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.panel button {
  height: 34px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  background: var(--semantic-color-state-danger);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}

.panel label {
  display: grid;
  gap: 4px;
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.panel textarea {
  min-height: 74px;
  border: 1px solid var(--semantic-component-input-border);
  border-radius: 8px;
  padding: 8px;
  color: var(--semantic-component-input-text);
  background: var(--semantic-component-input-bg);
  resize: vertical;
}

.panel--disabled {
  opacity: 0.7;
}
</style>
