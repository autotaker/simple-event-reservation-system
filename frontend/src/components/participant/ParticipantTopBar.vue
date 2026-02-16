<template>
  <header class="topbar">
    <div>
      <p class="topbar__event">{{ eventName }}</p>
      <h1 class="topbar__title">{{ dateLabel }}</h1>
    </div>
    <div class="topbar__guest">
      <span class="topbar__badge">{{ guestName }}</span>
      <button type="button" :disabled="disabled" @click="$emit('login')">ゲストでログイン</button>
    </div>
  </header>
</template>

<script setup lang="ts">
defineProps<{
  eventName: string;
  dateLabel: string;
  guestName: string;
  disabled?: boolean;
}>();

defineEmits<{
  login: [];
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
  border: 1px solid var(--semantic-color-participant-topbar-border);
  background: linear-gradient(
    145deg,
    var(--semantic-color-participant-topbar-bg-start),
    var(--semantic-color-participant-topbar-bg-end)
  );
}

.topbar__event {
  margin: 0;
  font-size: 12px;
  color: var(--semantic-color-participant-topbar-event-text);
}

.topbar__title {
  margin: 2px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: var(--semantic-color-participant-topbar-title-text);
}

.topbar__guest {
  display: flex;
  align-items: center;
  gap: 10px;
}

.topbar__badge {
  padding: var(--semantic-component-participant-badge-padding-y)
    var(--semantic-component-participant-badge-padding-x);
  border-radius: var(--semantic-component-participant-badge-radius);
  background: var(--semantic-color-participant-topbar-badge-bg);
  color: var(--semantic-color-participant-topbar-action-text);
  font-size: 12px;
  font-weight: 700;
}

.topbar button {
  height: var(--semantic-component-participant-button-topbar-height);
  border: none;
  border-radius: var(--semantic-component-participant-button-topbar-radius);
  padding: 0 var(--semantic-component-participant-button-padding-x);
  background: var(--semantic-color-participant-topbar-action-bg);
  color: var(--semantic-color-participant-topbar-action-text);
  font-size: var(--semantic-component-participant-button-text-size);
  font-weight: var(--semantic-component-participant-button-text-weight);
}

.topbar button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
