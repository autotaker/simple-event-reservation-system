<template>
  <header class="topbar">
    <div>
      <p class="topbar__event">{{ eventName }}</p>
      <h1 class="topbar__title">{{ sectionLabel }}</h1>
    </div>
    <div class="topbar__meta">
      <span class="topbar__badge">{{ adminName }}</span>
      <RouterLink class="topbar__link" :to="returnTo">{{ returnLabel }}</RouterLink>
    </div>
  </header>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router';

defineProps<{
  eventName: string;
  sectionLabel: string;
  adminName: string;
  returnTo: string;
  returnLabel: string;
}>();
</script>

<style scoped>
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid var(--semantic-color-state-warning);
  background: linear-gradient(
    145deg,
    var(--semantic-color-state-warning-soft),
    var(--semantic-color-bg-surface)
  );
}

.topbar__event {
  margin: 0;
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.topbar__title {
  margin: 2px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--semantic-color-text-primary);
}

.topbar__meta {
  display: flex;
  align-items: center;
  gap: 10px;
}

.topbar__badge {
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--semantic-color-state-warning);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}

.topbar__link {
  height: 34px;
  display: inline-flex;
  align-items: center;
  border-radius: 10px;
  padding: 0 12px;
  background: var(--semantic-color-state-warning);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
  text-decoration: none;
}
</style>
