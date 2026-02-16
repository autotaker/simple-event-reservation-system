<template>
  <article class="guard-mock" :class="`guard-mock--${state}`">
    <header class="guard-mock__header">
      <p class="guard-mock__route">{{ route }}</p>
      <h3 class="guard-mock__title">{{ title }}</h3>
      <p class="guard-mock__description">{{ description }}</p>
    </header>

    <section>
      <h4>表示する要素</h4>
      <ul>
        <li v-for="item in visibleItems" :key="item">{{ item }}</li>
      </ul>
    </section>

    <section>
      <h4>表示しない要素</h4>
      <ul>
        <li v-for="item in hiddenItems" :key="item">{{ item }}</li>
      </ul>
    </section>

    <footer class="guard-mock__footer">
      <button type="button" :disabled="state === 'loading'">
        {{ actionLabel }}
      </button>
      <p class="guard-mock__feedback" :class="`guard-mock__feedback--${state}`">
        {{ feedbackMessage }}
      </p>
      <p class="guard-mock__handoff">引き渡し先: #40</p>
    </footer>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(
  defineProps<{
    route: '/admin' | '/admin (forbidden)';
    title: string;
    description: string;
    visibleItems: string[];
    hiddenItems: string[];
    actionLabel: string;
    state?: 'success' | 'loading' | 'error';
  }>(),
  {
    state: 'success',
  },
);

const feedbackMessage = computed(() => {
  if (props.state === 'loading') {
    return '権限を確認しています。判定が完了するまで操作できません。';
  }
  if (props.state === 'error') {
    return '403 権限がありません。participant 導線へ戻ってください。';
  }
  return '管理導線が有効です。管理機能のみ表示されています。';
});
</script>

<style scoped>
.guard-mock {
  display: grid;
  gap: var(--space-4);
  width: min(760px, 100%);
  padding: var(--space-6);
  border: 2px solid var(--semantic-color-border-muted);
  border-radius: var(--radius-md);
  background: var(--semantic-color-bg-surface);
  color: var(--semantic-color-text-primary);
  font-family: var(--font-family-sans);
}

.guard-mock__header {
  display: grid;
  gap: 8px;
}

.guard-mock__route {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--semantic-color-text-muted);
  font-weight: var(--font-weight-semibold);
}

.guard-mock__title {
  margin: 0;
  font-size: var(--font-size-lg);
}

.guard-mock__description {
  margin: 0;
  line-height: 1.5;
}

.guard-mock h4 {
  margin: 0 0 6px;
  font-size: var(--font-size-sm);
}

.guard-mock ul {
  margin: 0;
  padding-left: 1.2rem;
  display: grid;
  gap: 4px;
}

.guard-mock__footer {
  display: grid;
  gap: 10px;
  padding-top: 4px;
}

.guard-mock button {
  width: fit-content;
  min-width: 210px;
  height: var(--semantic-component-button-height);
  padding-inline: var(--semantic-component-button-padding-x);
  border: none;
  border-radius: var(--semantic-component-button-radius);
  background: var(--semantic-component-button-bg);
  color: var(--semantic-component-button-text);
  font-weight: var(--font-weight-semibold);
}

.guard-mock button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.guard-mock__feedback {
  margin: 0;
  font-size: var(--font-size-sm);
}

.guard-mock__feedback--success {
  color: var(--semantic-color-state-success);
}

.guard-mock__feedback--loading {
  color: var(--semantic-color-text-muted);
}

.guard-mock__feedback--error {
  color: var(--semantic-color-state-danger);
}

.guard-mock__handoff {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--semantic-color-text-muted);
}

.guard-mock--success {
  border-color: #d97706;
}

.guard-mock--loading {
  border-color: #64748b;
}

.guard-mock--error {
  border-color: #dc2626;
}
</style>
