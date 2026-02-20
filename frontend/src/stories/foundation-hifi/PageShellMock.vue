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
  gap: 12px;
  width: min(960px, 100%);
  margin: 0 auto;
  padding: 16px;
  border: 1px solid var(--semantic-color-border-default);
  border-radius: 14px;
  background: var(--semantic-color-bg-surface);
  box-shadow: var(--semantic-layout-card-shadow);
}

.ui-shell--compact {
  width: min(390px, 100%);
  padding: 12px;
}
</style>
