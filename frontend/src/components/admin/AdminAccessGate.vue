<template>
  <section class="gate ui-panel">
    <h2>管理アクセス確認</h2>
    <label class="ui-field">
      <span class="ui-field__label">管理者トークン</span>
      <input v-model="model" class="ui-field__input" type="password" placeholder="admin token" />
    </label>
  </section>
</template>

<script setup lang="ts">
const model = defineModel<string>({ required: true });
</script>

<style scoped>
.gate {
  display: grid;
  gap: 8px;
}

.gate h2 {
  margin: 0;
}
</style>
