<template>
  <label class="ui-field" :class="{ 'ui-field--error': state === 'error' }">
    <span class="ui-field__label">{{ label }}</span>
    <input class="ui-field__input" :placeholder="placeholder" :disabled="disabled" />
    <span v-if="state === 'error'" class="ui-field__hint ui-field__hint--error">{{
      errorText
    }}</span>
    <span v-else class="ui-field__hint">{{ helperText }}</span>
  </label>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    label: string;
    placeholder?: string;
    helperText?: string;
    errorText?: string;
    state?: 'default' | 'error';
    disabled?: boolean;
  }>(),
  {
    placeholder: '',
    helperText: '英数字で入力してください。',
    errorText: '入力値が不正です。',
    state: 'default',
    disabled: false,
  },
);
</script>

<style scoped>
.ui-field {
  display: grid;
  gap: 6px;
}

.ui-field__label {
  font-size: 13px;
  font-weight: 600;
  color: var(--semantic-color-text-secondary);
}

.ui-field__input {
  height: var(--semantic-component-input-height);
  padding: 0 var(--semantic-component-input-padding-x);
  border: 1px solid var(--semantic-component-input-border);
  border-radius: var(--semantic-component-input-radius);
  color: var(--semantic-component-input-text);
  background: var(--semantic-component-input-bg);
}

.ui-field__input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.ui-field--error .ui-field__input {
  border-color: var(--semantic-color-state-danger);
}

.ui-field__hint {
  font-size: 12px;
  color: var(--semantic-color-text-muted);
}

.ui-field__hint--error {
  color: var(--semantic-color-state-danger);
}
</style>
