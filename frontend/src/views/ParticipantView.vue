<template>
  <main class="participant-portal ui-shell" :class="participantShellClass">
    <ParticipantTopBar
      event-name="Tokyo Product Summit 2026"
      :date-label="participantDateLabel"
      :guest-name="guestId || 'Guest'"
      :disabled="participantBusy"
      @login="runParticipantAction(loginAsGuest)"
    />

    <p v-if="errorMessage" class="participant-feedback ui-status ui-status--error">
      {{ errorMessage }}
    </p>
    <p v-if="infoMessage" class="participant-feedback ui-status ui-status--success">
      {{ infoMessage }}
    </p>

    <section class="participant-portal__body ui-layout-split">
      <section class="participant-sessions" aria-label="session list">
        <header class="participant-sessions__header">
          <h2>セッション一覧</h2>
          <button
            class="ui-button ui-button--secondary"
            type="button"
            :disabled="!hasToken || participantBusy"
            @click="runParticipantAction(loadSessions)"
          >
            更新
          </button>
        </header>
        <p v-if="hasToken && sessions.length === 0">セッション一覧は未取得です。</p>

        <ParticipantSessionTimetable
          v-if="sessions.length > 0"
          :sessions="sessions"
          :reserved-session-ids="reservations"
          :disabled="!hasToken || participantBusy"
          @reserve="
            (session) =>
              runParticipantAction(() => reserveSession(session.sessionId, session.title))
          "
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
          :qr-code-generation-status="qrCodeGenerationStatus"
          :reservations="myPageReservationItems"
          :has-token="hasToken"
          :disabled="participantBusy"
          @refresh="runParticipantAction(loadMyPage)"
        />
      </aside>
    </section>

    <p
      v-if="participantMode === 'loading'"
      class="participant-feedback ui-status ui-status--loading"
    >
      予約情報を更新中です...
    </p>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import ParticipantQrPanel from '../components/participant/ParticipantQrPanel.vue';
import ParticipantReservationPanel from '../components/participant/ParticipantReservationPanel.vue';
import ParticipantSessionTimetable from '../components/participant/ParticipantSessionTimetable.vue';
import ParticipantTopBar from '../components/participant/ParticipantTopBar.vue';
import { useReservationApp } from '../composables/useReservationApp';

type ParticipantReservationItem = {
  id: string;
  title: string;
  time: string;
};

type MyPageReservationItem = {
  id: string;
  title: string;
  meta: string;
  fallback?: boolean;
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
  qrCodeGenerationStatus,
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

const myPageReservationItems = computed<MyPageReservationItem[]>(() =>
  myPageReservations.value.map((reservationId) => {
    const session = sessions.value.find((candidate) => candidate.sessionId === reservationId);
    if (!session) {
      return {
        id: reservationId,
        title: '不明なセッション',
        meta: '更新して最新情報を取得してください',
        fallback: true,
      };
    }

    return {
      id: reservationId,
      title: session.title,
      meta: `${session.startTime} | ${session.track.trim().length > 0 ? session.track : 'Track 未設定'}`,
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

const participantShellClass = computed<Record<string, boolean>>(() => ({
  'ui-shell--participant': true,
  'ui-shell--participant-loading': participantMode.value === 'loading',
  'ui-shell--participant-success': participantMode.value === 'success',
  'ui-shell--participant-error': participantMode.value === 'error',
}));

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
  gap: var(--semantic-component-participant-portal-gap);
  padding: var(--semantic-component-participant-portal-padding);
  font-family: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
}

.participant-portal__body {
  gap: 12px;
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

.participant-side {
  display: grid;
  gap: 10px;
  align-content: start;
}

.participant-feedback {
  padding: var(--semantic-component-participant-feedback-padding-y)
    var(--semantic-component-participant-feedback-padding-x);
  border-radius: var(--semantic-component-participant-feedback-radius);
}

@media (max-width: 900px) {
  .participant-portal {
    padding: 12px;
  }

  .participant-portal__body {
    gap: 10px;
  }
}
</style>
