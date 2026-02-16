<template>
  <article class="session-card" :class="{ 'session-card--reserved': reserved }">
    <p class="session-card__meta">{{ startTime }} | {{ track }}</p>
    <h3 class="session-card__title">{{ title }}</h3>
    <div class="session-card__footer">
      <span :class="['session-card__seat', `session-card__seat--${seatTone}`]">{{
        seatLabel
      }}</span>
      <button type="button" :disabled="disabled" @click="$emit('reserve')">
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
  gap: 8px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #d7efe5;
  background: #ffffff;
}

.session-card__meta,
.session-card__title {
  margin: 0;
}

.session-card__meta {
  font-size: 12px;
  color: #4a7c67;
}

.session-card__title {
  font-size: 15px;
  color: #1c3f31;
}

.session-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.session-card__seat {
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.session-card__seat--open {
  background: #d8fbe7;
  color: #18613e;
}

.session-card__seat--few {
  background: #fff0cf;
  color: #925a00;
}

.session-card__seat--full {
  background: #ffe1e1;
  color: #a82626;
}

.session-card button {
  height: 32px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 700;
  background: #1f8e5f;
  color: #ffffff;
}

.session-card button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.session-card--reserved {
  background: #f4fbf8;
}
</style>
