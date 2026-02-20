<template>
  <section class="ui-shell" :class="{ 'ui-shell--compact': compact }">
    <slot />
  </section>
</template>

<script setup lang="ts">
defineProps<{
  compact?: boolean;
}>();
</script>

<style scoped>
.ui-shell {
  display: grid;
  gap: var(--space-3);
  width: min(960px, 100%);
  margin: 0 auto;
  padding: var(--space-4);
  border: 1px solid var(--semantic-color-border-default);
  border-radius: var(--radius-lg);
  background: var(--semantic-color-bg-surface);
  box-shadow: var(--semantic-layout-card-shadow);
}

.ui-shell--compact {
  width: min(390px, 100%);
  padding: var(--space-3);
}
</style>
