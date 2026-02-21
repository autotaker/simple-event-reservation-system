<template>
  <section class="card" :class="{ 'card--disabled': disabled }">
    <h3>管理者ログイン</h3>
    <div class="card__grid">
      <label>
        メールアドレス
        <input type="email" value="ops-admin@example.com" :disabled="disabled" />
      </label>
      <label>
        パスワード
        <input type="password" value="********" :disabled="disabled" />
      </label>
      <label>
        ワンタイムコード
        <input type="text" value="573911" :disabled="disabled" />
      </label>
    </div>
    <button type="button" :disabled="disabled">ログインして短命トークンを発行</button>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  disabled?: boolean;
}>();
</script>

<style scoped>
.card {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-warning);
  background: var(--semantic-color-bg-surface);
}

.card h3 {
  margin: 0;
  font-size: 14px;
}

.card__grid {
  display: grid;
  gap: 8px;
}

.card label {
  display: grid;
  gap: 4px;
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.card input {
  height: 32px;
  border: 1px solid var(--semantic-component-input-border);
  border-radius: 8px;
  padding: 0 8px;
  color: var(--semantic-component-input-text);
  background: var(--semantic-component-input-bg);
}

.card button {
  width: fit-content;
  min-width: 200px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--semantic-color-state-warning);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}

.card--disabled {
  opacity: 0.7;
}
</style>
