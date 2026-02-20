<template>
  <section class="operator-checkin-panel ui-panel">
    <h2>運営チェックイン</h2>
    <p v-if="!hasToken">運営チェックインはログイン中ユーザーのみ実行できます。</p>

    <template v-else>
      <label class="ui-field" for="checkin-qr-payload">
        <span class="ui-field__label">QR payload</span>
      </label>
      <textarea
        id="checkin-qr-payload"
        class="ui-field__input"
        :value="qrCodePayload"
        rows="3"
        cols="60"
        placeholder="event-reservation://checkin?guestId=...&reservations=..."
        @input="onPayloadInput"
      />

      <div class="operator-checkin-panel__actions">
        <button
          class="ui-button ui-button--primary"
          type="button"
          :disabled="disabled"
          @click="$emit('checkInEvent')"
        >
          イベント受付をチェックイン
        </button>
      </div>

      <div class="operator-checkin-panel__actions">
        <label class="ui-field__label" for="checkin-session-id">セッション受付</label>
        <select
          id="checkin-session-id"
          class="ui-field__input"
          :value="selectedSessionId"
          :disabled="disabled"
          @change="onSessionChange"
        >
          <option value="">選択してください</option>
          <option
            v-for="session in sessionOptions"
            :key="`checkin-${session.sessionId}`"
            :value="session.sessionId"
          >
            {{ session.startTime }} {{ session.title }}
          </option>
        </select>
        <button
          class="ui-button ui-button--secondary"
          type="button"
          :disabled="disabled || selectedSessionId.length === 0"
          @click="$emit('checkInSession')"
        >
          セッションをチェックイン
        </button>
      </div>

      <button
        class="ui-button ui-button--secondary"
        type="button"
        :disabled="disabled"
        @click="$emit('refreshHistory')"
      >
        チェックイン履歴を更新
      </button>

      <p
        v-if="resultMessage"
        class="operator-checkin-panel__feedback ui-status"
        :class="{
          'ui-status--success': resultTone === 'success',
          'ui-status--error': resultTone === 'error',
        }"
      >
        {{ resultMessage }}
      </p>

      <p v-if="historyLoaded && history.length === 0">チェックイン履歴はありません。</p>

      <div v-else-if="history.length > 0" class="ui-table-wrap">
        <table class="ui-table">
          <thead>
            <tr>
              <th scope="col">チェックイン種別</th>
              <th scope="col">ゲストID</th>
              <th scope="col">セッションID</th>
              <th scope="col">時刻</th>
            </tr>
          </thead>
          <tbody>
            <tr
              v-for="entry in history"
              :key="`${entry.checkInType}-${entry.sessionId ?? 'event'}-${entry.guestId}`"
            >
              <td>{{ entry.checkInTypeLabel }}</td>
              <td>{{ entry.guestId }}</td>
              <td>{{ entry.sessionId ?? '-' }}</td>
              <td>{{ entry.checkedInAtLabel }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
export type OperatorCheckInSessionOption = {
  sessionId: string;
  title: string;
  startTime: string;
};

export type OperatorCheckInHistoryRow = {
  guestId: string;
  checkInType: string;
  checkInTypeLabel: string;
  sessionId: string | null;
  checkedInAtLabel: string;
};

defineProps<{
  hasToken: boolean;
  disabled?: boolean;
  qrCodePayload: string;
  selectedSessionId: string;
  sessionOptions: OperatorCheckInSessionOption[];
  history: OperatorCheckInHistoryRow[];
  historyLoaded: boolean;
  resultMessage: string;
  resultTone: 'success' | 'error' | '';
}>();

const emit = defineEmits<{
  'update:qrCodePayload': [value: string];
  'update:selectedSessionId': [value: string];
  checkInEvent: [];
  checkInSession: [];
  refreshHistory: [];
}>();

const onPayloadInput = (event: globalThis.Event): void => {
  emit('update:qrCodePayload', (event.target as globalThis.HTMLTextAreaElement).value);
};

const onSessionChange = (event: globalThis.Event): void => {
  emit('update:selectedSessionId', (event.target as globalThis.HTMLSelectElement).value);
};
</script>

<style scoped>
.operator-checkin-panel {
  gap: 10px;
}

.operator-checkin-panel h2,
.operator-checkin-panel p {
  margin: 0;
}

.operator-checkin-panel textarea,
.operator-checkin-panel select {
  width: 100%;
  max-width: 100%;
}

.operator-checkin-panel textarea {
  min-height: 84px;
  padding-top: var(--space-2);
}

.operator-checkin-panel__actions {
  display: grid;
  gap: 6px;
}

.operator-checkin-panel__feedback {
  margin-top: 2px;
}
</style>
