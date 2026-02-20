<template>
  <main class="layout" :class="`layout--${mode}`">
    <ParticipantTopBarMock
      event-name="Tokyo Product Summit 2026"
      date-label="2/16 (Mon) Day 1"
      guest-name="Guest A12"
      :show-menu="isMobile"
    />
    <p v-if="feedbackMessage" class="layout__feedback" :class="`layout__feedback--${feedbackTone}`">
      {{ feedbackMessage }}
    </p>

    <section class="layout__body">
      <ParticipantSessionTimetableMock :disabled="mode === 'loading'" />

      <aside class="layout__side">
        <ParticipantReservationPanelMock
          :reservations="reservations"
          :disabled="mode === 'loading'"
        />
        <ParticipantQrPanelMock caption="受付QRコード" />
      </aside>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import ParticipantQrPanelMock from './ParticipantQrPanelMock.vue';
import ParticipantReservationPanelMock from './ParticipantReservationPanelMock.vue';
import ParticipantSessionTimetableMock from './ParticipantSessionTimetableMock.vue';
import ParticipantTopBarMock from './ParticipantTopBarMock.vue';

const props = defineProps<{
  mode: 'default' | 'loading' | 'success' | 'error';
  isMobile?: boolean;
}>();

const feedbackMessage = computed(() => {
  if (props.mode === 'loading') {
    return '予約情報を更新中です...';
  }
  if (props.mode === 'success') {
    return '11:00 Track A の予約を確定しました';
  }
  if (props.mode === 'error') {
    return '予約に失敗しました。時間を空けて再試行してください';
  }
  return '';
});

const feedbackTone = computed<'loading' | 'success' | 'error'>(() => {
  if (props.mode === 'success') {
    return 'success';
  }
  if (props.mode === 'error') {
    return 'error';
  }
  return 'loading';
});

const reservations = [
  { id: 'r1', title: 'Infra Talk', time: '11:00 | Track A' },
  { id: 'r2', title: 'Design Ops', time: '10:00 | Track B' },
];
</script>

<style scoped>
.layout {
  display: grid;
  gap: 12px;
  width: min(1080px, 100%);
  padding: 14px;
  border-radius: 18px;
  border: 1px solid #cde8de;
  background: linear-gradient(160deg, #f4fff9, #ffffff 48%, #f8fffd);
  font-family: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
}

.layout__body {
  display: grid;
  gap: 12px;
  grid-template-columns: 1.5fr 0.8fr;
}

.layout__feedback {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
}

.layout__feedback--loading {
  background: #fff7e0;
  color: #8c5a00;
}

.layout__feedback--success {
  background: #e8fbef;
  color: #1e6a45;
}

.layout__feedback--error {
  background: #ffe8e8;
  color: #9f2d2d;
}

.layout__side {
  display: grid;
  gap: 10px;
  align-content: start;
}

.layout--loading {
  border-color: #e9d09a;
}

.layout--success {
  border-color: #9bd7b4;
}

.layout--error {
  border-color: #e6a5a5;
}

@media (max-width: 960px) {
  .layout {
    width: min(390px, 100%);
    padding: 12px;
  }

  .layout__body {
    grid-template-columns: 1fr;
  }
}
</style>
