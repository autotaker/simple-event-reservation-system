<template>
  <article class="participant-mock" :class="`participant-mock--${status}`">
    <header class="participant-mock__header">
      <p class="participant-mock__route">{{ routeLabel }}</p>
      <h3 class="participant-mock__title">{{ title }}</h3>
      <p class="participant-mock__summary">{{ summary }}</p>
    </header>

    <section class="participant-mock__section">
      <h4>表示する機能</h4>
      <ul>
        <li v-for="item in participantFeatures" :key="item">{{ item }}</li>
      </ul>
    </section>

    <section class="participant-mock__section">
      <h4>表示しない機能</h4>
      <ul>
        <li v-for="item in adminFeatures" :key="item">{{ item }}</li>
      </ul>
    </section>

    <section class="participant-mock__section">
      <h4>操作とフィードバック</h4>
      <ul>
        <li v-for="item in feedbackRules" :key="item">{{ item }}</li>
      </ul>
    </section>

    <footer class="participant-mock__footer">
      <p>引き渡し先: {{ handoffIssue }}</p>
      <button type="button" :disabled="status === 'loading'">{{ actionLabel }}</button>
      <p v-if="status === 'loading'" class="participant-mock__feedback">更新中...</p>
      <p
        v-if="status === 'success'"
        class="participant-mock__feedback participant-mock__feedback--success"
      >
        予約操作が反映されました
      </p>
      <p
        v-if="status === 'error'"
        class="participant-mock__feedback participant-mock__feedback--error"
      >
        予約操作に失敗しました。内容を確認して再試行してください
      </p>
    </footer>
  </article>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    routeLabel: '/participant';
    title: string;
    summary: string;
    participantFeatures: string[];
    adminFeatures: string[];
    feedbackRules: string[];
    handoffIssue: '#39';
    actionLabel: string;
    status?: 'default' | 'loading' | 'success' | 'error';
  }>(),
  {
    status: 'default',
  },
);
</script>

<style scoped>
.participant-mock {
  display: grid;
  gap: var(--space-4);
  width: min(780px, 100%);
  padding: var(--space-6);
  border: 2px solid var(--semantic-color-border-muted);
  border-radius: var(--radius-md);
  background: var(--semantic-color-bg-surface);
  color: var(--semantic-color-text-primary);
  font-family: var(--font-family-sans);
}

.participant-mock__header {
  display: grid;
  gap: 8px;
}

.participant-mock__route {
  margin: 0;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--semantic-color-text-muted);
}

.participant-mock__title {
  margin: 0;
  font-size: var(--font-size-lg);
}

.participant-mock__summary {
  margin: 0;
  font-size: var(--font-size-md);
  line-height: 1.5;
}

.participant-mock__section h4 {
  margin: 0 0 6px;
  font-size: var(--font-size-sm);
}

.participant-mock__section ul {
  margin: 0;
  padding-left: 1.2rem;
  display: grid;
  gap: 4px;
}

.participant-mock__footer {
  display: grid;
  gap: 10px;
}

.participant-mock__footer p {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--semantic-color-text-muted);
}

.participant-mock button {
  width: fit-content;
  min-width: 220px;
  height: var(--semantic-component-button-height);
  padding-inline: var(--semantic-component-button-padding-x);
  border: none;
  border-radius: var(--semantic-component-button-radius);
  background: var(--semantic-component-button-bg);
  color: var(--semantic-component-button-text);
  font-weight: var(--font-weight-semibold);
}

.participant-mock button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.participant-mock__feedback {
  margin: 0;
  font-size: var(--font-size-sm);
}

.participant-mock__feedback--success {
  color: var(--semantic-color-state-success);
}

.participant-mock__feedback--error {
  color: var(--semantic-color-state-danger);
}

.participant-mock--default {
  border-color: #16a34a;
}

.participant-mock--loading {
  border-color: #a16207;
}

.participant-mock--success {
  border-color: #15803d;
}

.participant-mock--error {
  border-color: #b91c1c;
}
</style>
