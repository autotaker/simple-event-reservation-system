<template>
  <article class="session-card" :class="{ 'session-card--reserved': reserved }">
    <p class="session-card__meta">{{ startTime }} | {{ track }}</p>
    <h3 class="session-card__title">{{ title }}</h3>
    <div class="session-card__footer">
      <span :class="['session-card__seat', `session-card__seat--${seatTone}`]">{{
        seatLabel
      }}</span>
      <button
        class="ui-button ui-button--primary"
        type="button"
        :disabled="disabled"
        @click="$emit('reserve')"
      >
        {{ reserved ? '予約済み' : '予約する' }}
      </button>
    </div>
  </article>
</template>

<script setup lang="ts">
defineProps<{
  startTime: string;
  track: string;
  title: string;
  seatLabel: string;
  seatTone: 'open' | 'few' | 'full';
  reserved?: boolean;
  disabled?: boolean;
}>();

defineEmits<{
  reserve: [];
}>();
</script>

<style scoped>
.session-card {
  display: grid;
  gap: var(--semantic-component-participant-card-gap);
  padding: var(--semantic-component-participant-card-padding);
  border-radius: var(--semantic-component-participant-card-radius);
  border: 1px solid var(--semantic-color-participant-card-border);
  background: var(--semantic-color-participant-card-surface);
}

.session-card__meta,
.session-card__title {
  margin: 0;
}

.session-card__meta {
  font-size: 12px;
  color: var(--semantic-color-participant-card-meta-text);
}

.session-card__title {
  font-size: 15px;
  color: var(--semantic-color-participant-card-title-text);
}

.session-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.session-card__seat {
  padding: var(--semantic-component-participant-seat-padding-y)
    var(--semantic-component-participant-seat-padding-x);
  border-radius: var(--semantic-component-participant-seat-radius);
  font-size: 11px;
  font-weight: 700;
}

.session-card__seat--open {
  background: var(--semantic-color-participant-seat-open-bg);
  color: var(--semantic-color-participant-seat-open-text);
}

.session-card__seat--few {
  background: var(--semantic-color-participant-seat-few-bg);
  color: var(--semantic-color-participant-seat-few-text);
}

.session-card__seat--full {
  background: var(--semantic-color-participant-seat-full-bg);
  color: var(--semantic-color-participant-seat-full-text);
}

.session-card :deep(.ui-button) {
  min-height: var(--semantic-component-participant-button-primary-height);
  border-radius: var(--semantic-component-participant-button-primary-radius);
  font-size: var(--semantic-component-participant-button-text-size);
  font-weight: var(--semantic-component-participant-button-text-weight);
  background: var(--semantic-color-participant-action-primary-bg);
  color: var(--semantic-color-participant-action-primary-text);
}

.session-card--reserved {
  background: var(--semantic-color-participant-card-reserved-bg);
}
</style>
