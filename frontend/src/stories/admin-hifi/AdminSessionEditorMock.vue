<template>
  <section class="editor" :class="{ 'editor--disabled': disabled }">
    <h3>セッション作成/編集</h3>
    <div class="editor__grid">
      <label>
        タイトル
        <input type="text" value="AI Safety in Practice" :disabled="disabled" />
      </label>
      <label>
        トラック
        <input type="text" value="Track Ops" :disabled="disabled" />
      </label>
      <label>
        開始時刻
        <input type="text" value="13:30" :disabled="disabled" />
      </label>
      <label>
        定員
        <input type="number" value="120" :disabled="disabled" />
      </label>
    </div>
    <button type="button" :disabled="disabled">保存する</button>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  disabled?: boolean;
}>();
</script>

<style scoped>
.editor {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-border-strong);
  background: var(--semantic-color-bg-surface);
}

.editor h3 {
  margin: 0;
  font-size: 14px;
}

.editor__grid {
  display: grid;
  gap: 8px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.editor label {
  display: grid;
  gap: 4px;
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.editor input {
  height: 32px;
  border: 1px solid var(--semantic-component-input-border);
  border-radius: 8px;
  padding: 0 8px;
  color: var(--semantic-component-input-text);
  background: var(--semantic-component-input-bg);
}

.editor button {
  width: fit-content;
  min-width: 120px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--semantic-component-button-bg);
  color: var(--semantic-component-button-text);
  font-size: 12px;
  font-weight: 700;
}

.editor--disabled {
  opacity: 0.7;
}

@media (max-width: 900px) {
  .editor__grid {
    grid-template-columns: 1fr;
  }
}
</style>
