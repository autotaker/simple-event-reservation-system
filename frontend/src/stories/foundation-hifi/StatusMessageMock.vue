<template>
  <p class="ui-status" :class="`ui-status--${tone}`">
    {{ message }}
  </p>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    tone?: 'info' | 'loading' | 'success' | 'error';
    message: string;
  }>(),
  {
    tone: 'info',
  },
);
</script>

<style scoped>
.ui-status {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
}

.ui-status--info {
  background: var(--semantic-color-bg-subtle);
  color: var(--semantic-color-text-secondary);
}

.ui-status--loading {
  background: var(--semantic-color-state-warning-soft);
  color: var(--semantic-color-state-warning);
}

.ui-status--success {
  background: var(--semantic-color-state-success-soft);
  color: var(--semantic-color-state-success);
}

.ui-status--error {
  background: var(--semantic-color-state-danger-soft);
  color: var(--semantic-color-state-danger);
}
</style>
