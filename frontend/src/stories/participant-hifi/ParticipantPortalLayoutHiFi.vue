<template>
  <main class="portal" :class="`portal--${mode}`">
    <ParticipantTopBarMock
      event-name="Tokyo Product Summit 2026"
      date-label="2/16 (Mon) Day 1"
      guest-name="Guest A12"
      :show-menu="isMobile"
    />

    <section class="portal__body">
      <section class="portal__sessions" aria-label="session list">
        <ParticipantSessionCardMock
          v-for="card in sessionCards"
          :key="card.id"
          :start-time="card.startTime"
          :track="card.track"
          :title="card.title"
          :seat-label="card.seatLabel"
          :seat-tone="card.seatTone"
          :reserved="card.reserved"
          :disabled="mode === 'loading'"
        />
      </section>

      <aside class="portal__side">
        <ParticipantReservationPanelMock
          :reservations="reservations"
          :disabled="mode === 'loading'"
        />
        <ParticipantQrPanelMock caption="受付QRコード" />
      </aside>
    </section>

    <p v-if="mode === 'loading'" class="portal__feedback portal__feedback--loading">
      予約情報を更新中です...
    </p>
    <p v-if="mode === 'success'" class="portal__feedback portal__feedback--success">
      予約を確定しました
    </p>
    <p v-if="mode === 'error'" class="portal__feedback portal__feedback--error">
      接続エラーです。再試行してください
    </p>
  </main>
</template>

<script setup lang="ts">
import ParticipantQrPanelMock from './ParticipantQrPanelMock.vue';
import ParticipantReservationPanelMock from './ParticipantReservationPanelMock.vue';
import ParticipantSessionCardMock from './ParticipantSessionCardMock.vue';
import ParticipantTopBarMock from './ParticipantTopBarMock.vue';

defineProps<{
  mode: 'default' | 'loading' | 'success' | 'error';
  isMobile?: boolean;
}>();

const sessionCards = [
  {
    id: 's1',
    startTime: '10:00',
    track: 'Track A',
    title: 'Keynote: Product Vision',
    seatLabel: '空席あり',
    seatTone: 'open' as const,
  },
  {
    id: 's2',
    startTime: '11:30',
    track: 'Track B',
    title: 'API Design Clinic',
    seatLabel: '残りわずか',
    seatTone: 'few' as const,
    reserved: true,
  },
];

const reservations = [
  { id: 'r1', title: 'Keynote: Product Vision', time: '10:00 | Track A' },
  { id: 'r2', title: 'API Design Clinic', time: '11:30 | Track B' },
];
</script>

<style scoped>
.portal {
  display: grid;
  gap: 12px;
  width: min(980px, 100%);
  padding: 14px;
  border-radius: 18px;
  border: 1px solid #cde8de;
  background: linear-gradient(160deg, #f4fff9, #ffffff 48%, #f8fffd);
  font-family: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
}

.portal__body {
  display: grid;
  gap: 12px;
  grid-template-columns: 1.35fr 1fr;
}

.portal__sessions {
  display: grid;
  gap: 10px;
}

.portal__side {
  display: grid;
  gap: 10px;
  align-content: start;
}

.portal__feedback {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
}

.portal__feedback--loading {
  background: #fff6df;
  color: #8c5a00;
}

.portal__feedback--success {
  background: #e8fbef;
  color: #1e6a45;
}

.portal__feedback--error {
  background: #ffe8e8;
  color: #9f2d2d;
}

.portal--loading {
  border-color: #e9d09a;
}

.portal--success {
  border-color: #9bd7b4;
}

.portal--error {
  border-color: #e6a5a5;
}

@media (max-width: 900px) {
  .portal {
    width: min(390px, 100%);
    padding: 12px;
  }

  .portal__body {
    grid-template-columns: 1fr;
  }
}
</style>
