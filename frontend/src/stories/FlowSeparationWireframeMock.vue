<template>
  <article class="flow-mock" :class="`flow-mock--${tone}`">
    <header class="flow-mock__header">
      <p class="flow-mock__route">{{ routeLabel }}</p>
      <h3 class="flow-mock__title">{{ title }}</h3>
    </header>
    <p class="flow-mock__summary">{{ summary }}</p>

    <section class="flow-mock__allowed">
      <h4>表示する機能</h4>
      <ul>
        <li v-for="item in allowedFeatures" :key="item">{{ item }}</li>
      </ul>
    </section>

    <section class="flow-mock__blocked">
      <h4>表示しない機能</h4>
      <ul>
        <li v-for="item in blockedFeatures" :key="item">{{ item }}</li>
      </ul>
    </section>

    <footer class="flow-mock__footer">
      <p class="flow-mock__handoff">引き渡し先: {{ handoffIssue }}</p>
      <button type="button" :disabled="status === 'loading'">
        {{ actionLabel }}
      </button>
      <p v-if="status === 'loading'" class="flow-mock__feedback">判定中...</p>
      <p v-if="status === 'success'" class="flow-mock__feedback flow-mock__feedback--success">
        導線仕様に沿って表示制御されています
      </p>
      <p v-if="status === 'error'" class="flow-mock__feedback flow-mock__feedback--error">
        権限外導線のためアクセスできません
      </p>
    </footer>
  </article>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    routeLabel: '/participant' | '/admin' | '/admin (forbidden)';
    title: string;
    summary: string;
    allowedFeatures: string[];
    blockedFeatures: string[];
    handoffIssue: '#39' | '#40';
    actionLabel: string;
    status?: 'default' | 'loading' | 'success' | 'error';
    tone?: 'participant' | 'admin' | 'forbidden';
  }>(),
  {
    status: 'default',
    tone: 'participant',
  },
);
</script>

<style scoped>
.flow-mock {
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

.flow-mock__route {
  margin: 0;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--semantic-color-text-muted);
}

.flow-mock__title {
  margin: 4px 0 0;
  font-size: var(--font-size-lg);
}

.flow-mock__summary {
  margin: 0;
  font-size: var(--font-size-md);
  line-height: 1.5;
}

.flow-mock h4 {
  margin: 0 0 6px;
  font-size: var(--font-size-sm);
}

.flow-mock ul {
  margin: 0;
  padding-left: 1.2rem;
  display: grid;
  gap: 4px;
}

.flow-mock__footer {
  display: grid;
  gap: 10px;
  padding-top: 6px;
}

.flow-mock__handoff {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--semantic-color-text-muted);
}

.flow-mock button {
  width: fit-content;
  min-width: 180px;
  height: var(--semantic-component-button-height);
  padding-inline: var(--semantic-component-button-padding-x);
  border: none;
  border-radius: var(--semantic-component-button-radius);
  background: var(--semantic-component-button-bg);
  color: var(--semantic-component-button-text);
  font-weight: var(--font-weight-semibold);
}

.flow-mock button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.flow-mock__feedback {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--semantic-color-text-muted);
}

.flow-mock__feedback--success {
  color: var(--semantic-color-state-success);
}

.flow-mock__feedback--error {
  color: var(--semantic-color-state-danger);
}

.flow-mock--participant {
  border-color: #16a34a;
}

.flow-mock--admin {
  border-color: #d97706;
}

.flow-mock--forbidden {
  border-color: #dc2626;
}
</style>
