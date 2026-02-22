<template>
  <section class="denied">
    <p class="denied__code">401 Unauthorized</p>
    <h3>{{ heading }}</h3>
    <p class="denied__reason">{{ message }}</p>
    <button type="button">再ログインする</button>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  heading: string;
  message: string;
}>();
</script>

<style scoped>
.denied {
  display: grid;
  gap: 8px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-danger);
  background: var(--semantic-color-state-danger-soft);
}

.denied__code,
.denied h3,
.denied__reason {
  margin: 0;
}

.denied__code {
  font-size: 12px;
  font-weight: 700;
  color: var(--semantic-color-state-danger);
}

.denied h3 {
  font-size: 14px;
  color: var(--semantic-color-text-primary);
}

.denied__reason {
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
  line-height: 1.4;
}

.denied button {
  width: fit-content;
  min-width: 130px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--semantic-color-state-danger);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}
</style>
