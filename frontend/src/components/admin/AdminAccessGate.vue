<template>
  <section class="gate">
    <h2>管理アクセス確認</h2>
    <label>
      管理者トークン
      <input v-model="model" type="password" placeholder="admin token" />
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

.gate label {
  display: grid;
  gap: 6px;
}

.gate input {
  max-width: 280px;
}
</style>
