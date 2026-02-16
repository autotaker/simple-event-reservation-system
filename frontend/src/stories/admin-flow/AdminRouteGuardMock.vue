<template>
  <article class="admin-guard" :class="`admin-guard--${stateTone}`">
    <header class="admin-guard__header">
      <p class="admin-guard__route">{{ route }}</p>
      <h3 class="admin-guard__title">{{ title }}</h3>
      <p class="admin-guard__summary">{{ summary }}</p>
    </header>

    <section class="admin-guard__panel">
      <h4>表示する領域</h4>
      <ul>
        <li v-for="item in visibleBlocks" :key="item">{{ item }}</li>
      </ul>
    </section>

    <section class="admin-guard__panel admin-guard__panel--muted">
      <h4>表示しない領域</h4>
      <ul>
        <li v-for="item in hiddenBlocks" :key="item">{{ item }}</li>
      </ul>
    </section>

    <footer class="admin-guard__footer">
      <button type="button" :disabled="state === 'loading'">{{ primaryAction }}</button>
      <p class="admin-guard__feedback" :class="`admin-guard__feedback--${state}`">{{ feedback }}</p>
    </footer>
  </article>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    route: '/admin' | '/admin (forbidden)';
    title: string;
    summary: string;
    visibleBlocks: string[];
    hiddenBlocks: string[];
    primaryAction: string;
    feedback: string;
    state?: 'default' | 'loading' | 'error' | 'success';
    stateTone?: 'admin' | 'forbidden';
  }>(),
  {
    state: 'default',
    stateTone: 'admin',
  },
);
</script>

<style scoped>
.admin-guard {
  display: grid;
  gap: var(--space-4);
  width: min(780px, 100%);
  padding: var(--space-6);
  border: 2px solid #b45309;
  border-radius: var(--radius-md);
  background: linear-gradient(145deg, #fffbeb 0%, #ffffff 70%);
  color: var(--semantic-color-text-primary);
  font-family: var(--font-family-sans);
}

.admin-guard__header {
  display: grid;
  gap: 6px;
}

.admin-guard__route {
  margin: 0;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: #92400e;
}

.admin-guard__title {
  margin: 0;
  font-size: var(--font-size-xl);
}

.admin-guard__summary {
  margin: 0;
  font-size: var(--font-size-md);
  line-height: 1.5;
}

.admin-guard__panel {
  display: grid;
  gap: 8px;
  padding: var(--space-4);
  border: 1px solid #fcd34d;
  border-radius: var(--radius-sm);
  background: #fefce8;
}

.admin-guard__panel--muted {
  border-color: #d1d5db;
  background: #f9fafb;
}

.admin-guard__panel h4 {
  margin: 0;
  font-size: var(--font-size-sm);
}

.admin-guard__panel ul {
  display: grid;
  gap: 4px;
  margin: 0;
  padding-left: 1.2rem;
}

.admin-guard__footer {
  display: grid;
  gap: 10px;
}

.admin-guard button {
  width: fit-content;
  min-width: 220px;
  height: var(--semantic-component-button-height);
  padding-inline: var(--semantic-component-button-padding-x);
  border: none;
  border-radius: var(--semantic-component-button-radius);
  background: #92400e;
  color: #fff;
  font-weight: var(--font-weight-semibold);
}

.admin-guard button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.admin-guard__feedback {
  margin: 0;
  font-size: var(--font-size-sm);
  color: var(--semantic-color-text-muted);
}

.admin-guard__feedback--success {
  color: #047857;
}

.admin-guard__feedback--error {
  color: #b91c1c;
}

.admin-guard__feedback--loading {
  color: #2563eb;
}

.admin-guard--forbidden {
  border-color: #b91c1c;
  background: linear-gradient(145deg, #fef2f2 0%, #ffffff 70%);
}

.admin-guard--forbidden .admin-guard__route {
  color: #b91c1c;
}

.admin-guard--forbidden .admin-guard__panel {
  border-color: #fecaca;
  background: #fff1f2;
}

.admin-guard--forbidden button {
  background: #b91c1c;
}
</style>
