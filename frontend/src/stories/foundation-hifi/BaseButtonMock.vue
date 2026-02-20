<template>
  <button class="ui-button" :class="[`ui-button--${tone}`]" :disabled="disabled" type="button">
    {{ label }}
  </button>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    label: string;
    tone?: 'primary' | 'secondary';
    disabled?: boolean;
  }>(),
  {
    tone: 'primary',
    disabled: false,
  },
);
</script>

<style scoped>
.ui-button {
  height: var(--semantic-component-button-height);
  padding: 0 var(--semantic-component-button-padding-x);
  border: 1px solid transparent;
  border-radius: var(--semantic-component-button-radius);
  font-size: var(--semantic-component-button-font-size);
  font-weight: var(--semantic-component-button-font-weight);
  color: var(--semantic-color-text-on-primary);
  cursor: pointer;
}

.ui-button--primary {
  background: var(--semantic-color-action-primary);
}

.ui-button--secondary {
  background: var(--semantic-color-action-primary-soft);
  border-color: var(--semantic-color-border-default);
  color: var(--semantic-color-text-primary);
}

.ui-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
