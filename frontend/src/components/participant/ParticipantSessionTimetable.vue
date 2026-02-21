<template>
  <section class="participant-timetable" aria-label="session timetable">
    <div class="participant-timetable__legend">
      <span class="participant-timetable__chip participant-timetable__chip--open">受付中</span>
      <span class="participant-timetable__chip participant-timetable__chip--few">残りわずか</span>
      <span class="participant-timetable__chip participant-timetable__chip--full">満席</span>
      <span class="participant-timetable__chip participant-timetable__chip--reserved"
        >予約済み</span
      >
    </div>

    <div class="ui-table-wrap participant-timetable__scroll">
      <table class="ui-table participant-timetable__table">
        <thead>
          <tr>
            <th scope="col" class="participant-timetable__time-col">Time</th>
            <th v-for="track in tracks" :key="track" scope="col">{{ track }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="startTime in startTimes" :key="startTime">
            <th scope="row" class="participant-timetable__time-col">{{ startTime }}</th>
            <td v-for="track in tracks" :key="`${startTime}-${track}`">
              <div
                v-if="sessionsByCellKey[`${startTime}::${track}`]"
                class="participant-timetable__cell-list"
              >
                <article
                  v-for="session in sessionsByCellKey[`${startTime}::${track}`]"
                  :key="session.sessionId"
                  class="participant-timetable__cell"
                  :class="cellClass(session)"
                >
                  <h3 class="participant-timetable__title">
                    {{ session.title }}
                  </h3>
                  <p class="participant-timetable__status">
                    {{ cellStatusLabel(session) }}
                  </p>
                  <button
                    class="ui-button ui-button--primary participant-timetable__action"
                    type="button"
                    :disabled="isCellDisabled(session)"
                    @click="$emit('reserve', session)"
                  >
                    {{ cellButtonLabel(session) }}
                  </button>
                </article>
              </div>
              <article
                v-else
                class="participant-timetable__cell participant-timetable__cell--empty"
              >
                <p class="participant-timetable__title participant-timetable__title--empty">
                  セッションなし
                </p>
                <p class="participant-timetable__status participant-timetable__status--empty">-</p>
                <button
                  class="ui-button ui-button--primary participant-timetable__action"
                  type="button"
                  disabled
                >
                  予約不可
                </button>
              </article>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { SessionAvailabilityStatus } from '../../composables/useReservationApp';

type TimetableSession = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  availabilityStatus: SessionAvailabilityStatus;
};

const props = withDefaults(
  defineProps<{
    sessions: TimetableSession[];
    reservedSessionIds: string[];
    disabled?: boolean;
  }>(),
  {
    disabled: false,
  },
);

defineEmits<{
  reserve: [session: TimetableSession];
}>();

const trackSort = (left: string, right: string): number => left.localeCompare(right, 'ja');
const timeSort = (left: string, right: string): number => left.localeCompare(right, 'ja');

const tracks = computed<string[]>(() =>
  Array.from(new Set(props.sessions.map((session) => session.track))).sort(trackSort),
);

const startTimes = computed<string[]>(() =>
  Array.from(new Set(props.sessions.map((session) => session.startTime))).sort(timeSort),
);

const reservedSessionIdSet = computed<Set<string>>(() => new Set(props.reservedSessionIds));

const sessionsByCellKey = computed<Record<string, TimetableSession[]>>(() =>
  props.sessions.reduce<Record<string, TimetableSession[]>>((map, session) => {
    const key = `${session.startTime}::${session.track}`;
    if (!map[key]) {
      map[key] = [];
    }
    map[key].push(session);
    return map;
  }, {}),
);

const isReserved = (session: TimetableSession): boolean =>
  reservedSessionIdSet.value.has(session.sessionId);

const isCellDisabled = (session: TimetableSession): boolean =>
  props.disabled || session.availabilityStatus === 'FULL' || isReserved(session);

const cellClass = (session: TimetableSession): string => {
  if (isReserved(session)) {
    return 'participant-timetable__cell--reserved';
  }
  if (session.availabilityStatus === 'FEW_LEFT') {
    return 'participant-timetable__cell--few';
  }
  if (session.availabilityStatus === 'FULL') {
    return 'participant-timetable__cell--full';
  }
  return 'participant-timetable__cell--open';
};

const cellStatusLabel = (session: TimetableSession): string => {
  if (isReserved(session)) {
    return '予約済み';
  }
  if (session.availabilityStatus === 'FEW_LEFT') {
    return '残りわずか';
  }
  if (session.availabilityStatus === 'FULL') {
    return '満席';
  }
  return '受付中';
};

const cellButtonLabel = (session: TimetableSession): string => {
  if (isReserved(session)) {
    return '予約済み';
  }
  if (session.availabilityStatus === 'FULL') {
    return '満席';
  }
  return '予約する';
};
</script>

<style scoped>
.participant-timetable {
  display: grid;
  gap: var(--space-2);
}

.participant-timetable__legend {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-2);
}

.participant-timetable__chip {
  padding: var(--space-1) var(--space-2);
  border-radius: var(--semantic-component-participant-seat-radius);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-bold);
}

.participant-timetable__chip--open {
  background: var(--semantic-color-participant-seat-open-bg);
  color: var(--semantic-color-participant-seat-open-text);
}

.participant-timetable__chip--few {
  background: var(--semantic-color-participant-seat-few-bg);
  color: var(--semantic-color-participant-seat-few-text);
}

.participant-timetable__chip--full {
  background: var(--semantic-color-participant-seat-full-bg);
  color: var(--semantic-color-participant-seat-full-text);
}

.participant-timetable__chip--reserved {
  background: var(--semantic-color-participant-card-reserved-bg);
  color: var(--semantic-color-participant-card-title-text);
}

.participant-timetable__scroll {
  overflow-x: auto;
}

.participant-timetable__table {
  min-width: 720px;
  table-layout: fixed;
}

.participant-timetable__cell-list {
  display: grid;
  gap: var(--space-2);
}

.participant-timetable__time-col {
  width: 84px;
  white-space: nowrap;
}

.participant-timetable__cell {
  display: grid;
  gap: var(--space-2);
  min-height: 136px;
  padding: var(--space-2);
  border-radius: var(--semantic-component-participant-card-radius);
  border: 1px solid var(--semantic-color-participant-card-border);
}

.participant-timetable__cell--open {
  background: var(--semantic-color-participant-card-surface);
}

.participant-timetable__cell--few {
  background: var(--semantic-color-participant-seat-few-bg);
}

.participant-timetable__cell--full {
  background: var(--semantic-color-participant-seat-full-bg);
}

.participant-timetable__cell--reserved {
  background: var(--semantic-color-participant-card-reserved-bg);
}

.participant-timetable__cell--empty {
  background: var(--semantic-color-bg-subtle);
}

.participant-timetable__title,
.participant-timetable__status {
  margin: 0;
}

.participant-timetable__title {
  font-size: var(--font-size-sm);
  color: var(--semantic-color-participant-card-title-text);
}

.participant-timetable__title--empty,
.participant-timetable__status--empty {
  color: var(--semantic-color-text-muted);
}

.participant-timetable__status {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-semibold);
  color: var(--semantic-color-participant-card-meta-text);
}

.participant-timetable__action {
  width: 100%;
  min-height: var(--semantic-component-participant-button-primary-height);
  border-radius: var(--semantic-component-participant-button-primary-radius);
  font-size: var(--semantic-component-participant-button-text-size);
  font-weight: var(--semantic-component-participant-button-text-weight);
  background: var(--semantic-color-participant-action-primary-bg);
  color: var(--semantic-color-participant-action-primary-text);
}
</style>
