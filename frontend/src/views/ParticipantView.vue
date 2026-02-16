<template>
  <main class="participant-portal" :class="`participant-portal--${participantMode}`">
    <ParticipantTopBar
      event-name="Tokyo Product Summit 2026"
      :date-label="participantDateLabel"
      :guest-name="guestId || 'Guest'"
      :disabled="participantBusy"
      @login="runParticipantAction(loginAsGuest)"
    />

    <p v-if="errorMessage" class="participant-feedback participant-feedback--error">
      {{ errorMessage }}
    </p>
    <p v-if="infoMessage" class="participant-feedback participant-feedback--success">
      {{ infoMessage }}
    </p>

    <section class="participant-portal__body">
      <section class="participant-sessions" aria-label="session list">
        <header class="participant-sessions__header">
          <h2>セッション一覧</h2>
          <button
            type="button"
            :disabled="!hasToken || participantBusy"
            @click="runParticipantAction(loadSessions)"
          >
            更新
          </button>
        </header>
        <p v-if="hasToken && sessions.length === 0">セッション一覧は未取得です。</p>

        <ParticipantSessionCard
          v-for="session in sessions"
          :key="session.sessionId"
          :start-time="session.startTime"
          :track="session.track"
          :title="session.title"
          :seat-label="availabilityStatusLabel(session.availabilityStatus)"
          :seat-tone="availabilitySeatTone(session.availabilityStatus)"
          :reserved="isSessionReserved(session.sessionId)"
          :disabled="
            !hasToken ||
            participantBusy ||
            session.availabilityStatus === 'FULL' ||
            isSessionReserved(session.sessionId)
          "
          @reserve="runParticipantAction(() => reserveSession(session.sessionId, session.title))"
        />
      </section>

      <aside class="participant-side">
        <ParticipantReservationPanel
          :reservations="participantReservationItems"
          :has-token="hasToken"
          :registered="registered"
          :registration-status-loaded="registrationStatusLoaded"
          :disabled="participantBusy"
          @refresh="runParticipantAction(loadReservations)"
          @reserve-keynote="runParticipantAction(reserveKeynote)"
          @cancel="(reservationId) => runParticipantAction(() => cancelReservation(reservationId))"
        />

        <ParticipantQrPanel
          :qr-code-payload="myPageQrCodePayload"
          :qr-code-image-url="receptionQrCodeImageUrl"
          :reservations="myPageReservations"
          :has-token="hasToken"
          :disabled="participantBusy"
          @refresh="runParticipantAction(loadMyPage)"
        />
      </aside>
    </section>

    <p
      v-if="participantMode === 'loading'"
      class="participant-feedback participant-feedback--loading"
    >
      予約情報を更新中です...
    </p>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import ParticipantQrPanel from '../components/participant/ParticipantQrPanel.vue';
import ParticipantReservationPanel from '../components/participant/ParticipantReservationPanel.vue';
import ParticipantSessionCard from '../components/participant/ParticipantSessionCard.vue';
import ParticipantTopBar from '../components/participant/ParticipantTopBar.vue';
import {
  type SessionAvailabilityStatus,
  useReservationApp,
} from '../composables/useReservationApp';

type ParticipantReservationItem = {
  id: string;
  title: string;
  time: string;
};

const {
  token,
  guestId,
  sessions,
  reservations,
  myPageReservations,
  myPageQrCodePayload,
  registered,
  registrationStatusLoaded,
  errorMessage,
  infoMessage,
  receptionQrCodeImageUrl,
  availabilityStatusLabel,
  isSessionReserved,
  loginAsGuest,
  loadSessions,
  loadReservations,
  loadMyPage,
  reserveSession,
  reserveKeynote,
  cancelReservation,
  loadParticipantBootstrap,
} = useReservationApp();

const participantBusy = ref<boolean>(false);
const hasToken = computed<boolean>(() => token.value !== null);

const participantDateLabel = computed(() => {
  const now = new Date();
  const monthDay = new Intl.DateTimeFormat('en-US', { month: 'numeric', day: 'numeric' }).format(
    now,
  );
  const weekday = new Intl.DateTimeFormat('en-US', { weekday: 'short' }).format(now);
  return `${monthDay} (${weekday}) Day 1`;
});

const participantReservationItems = computed<ParticipantReservationItem[]>(() =>
  reservations.value.map((reservationId) => {
    const session = sessions.value.find((candidate) => candidate.sessionId === reservationId);
    if (session) {
      return {
        id: reservationId,
        title: session.title,
        time: `${session.startTime} | ${session.track}`,
      };
    }

    return {
      id: reservationId,
      title: reservationId === 'keynote' ? 'キーノート' : reservationId,
      time: reservationId === 'keynote' ? '基調講演' : '',
    };
  }),
);

const participantMode = computed<'default' | 'loading' | 'success' | 'error'>(() => {
  if (participantBusy.value) {
    return 'loading';
  }
  if (errorMessage.value) {
    return 'error';
  }
  if (infoMessage.value) {
    return 'success';
  }
  return 'default';
});

const availabilitySeatTone = (status: SessionAvailabilityStatus): 'open' | 'few' | 'full' => {
  if (status === 'OPEN') {
    return 'open';
  }
  if (status === 'FEW_LEFT') {
    return 'few';
  }
  return 'full';
};

const runParticipantAction = async (action: () => Promise<void>): Promise<void> => {
  participantBusy.value = true;
  try {
    await action();
  } finally {
    participantBusy.value = false;
  }
};

onMounted(() => {
  void loadParticipantBootstrap();
});
</script>

<style scoped>
.participant-portal {
  display: grid;
  gap: 12px;
  width: min(980px, 100%);
  margin: 0 auto;
  padding: 14px;
  border-radius: 18px;
  border: 1px solid #cde8de;
  background: linear-gradient(160deg, #f4fff9, #ffffff 48%, #f8fffd);
  font-family: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
}

.participant-portal__body {
  display: grid;
  gap: 12px;
  grid-template-columns: 1.35fr 1fr;
}

.participant-sessions {
  display: grid;
  gap: 10px;
}

.participant-sessions__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.participant-sessions h2 {
  margin: 0;
  font-size: 14px;
}

.participant-sessions__header button {
  height: 30px;
  padding: 0 10px;
  border: none;
  border-radius: 8px;
  background: #315e96;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

.participant-sessions__header button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.participant-side {
  display: grid;
  gap: 10px;
  align-content: start;
}

.participant-feedback {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
}

.participant-feedback--loading {
  background: #fff6df;
  color: #8c5a00;
}

.participant-feedback--success {
  background: #e8fbef;
  color: #1e6a45;
}

.participant-feedback--error {
  background: #ffe8e8;
  color: #9f2d2d;
}

.participant-portal--loading {
  border-color: #e9d09a;
}

.participant-portal--success {
  border-color: #9bd7b4;
}

.participant-portal--error {
  border-color: #e6a5a5;
}

@media (max-width: 900px) {
  .participant-portal {
    width: min(390px, 100%);
    padding: 12px;
  }

  .participant-portal__body {
    grid-template-columns: 1fr;
  }
}
</style>
