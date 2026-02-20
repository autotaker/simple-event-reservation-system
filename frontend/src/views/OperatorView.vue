<template>
  <main>
    <h1>Event Reservation MVP</h1>
    <p>ゲストでログインしてキーノート予約を行えます。</p>

    <button type="button" @click="loginAsGuest">ゲストでログイン</button>
    <p v-if="guestId">ログイン中: {{ guestId }}</p>
    <p v-if="errorMessage">{{ errorMessage }}</p>
    <p v-if="infoMessage">{{ infoMessage }}</p>

    <section>
      <h2>セッション管理（運営）</h2>
      <label>
        管理者トークン
        <input v-model="adminToken" type="password" placeholder="admin token" />
      </label>
      <button type="button" :disabled="!adminToken" @click="loadAdminSessions">
        管理一覧を取得
      </button>
      <button type="button" :disabled="!adminToken" @click="downloadReservationCsv">
        予約一覧CSVを出力
      </button>
      <button type="button" :disabled="!adminToken" @click="downloadSessionCheckInCsv">
        チェックインCSVを出力
      </button>

      <form @submit.prevent="createAdminSession">
        <h3>新規作成</h3>
        <label>
          タイトル
          <input v-model="createForm.title" type="text" required />
        </label>
        <label>
          開始時刻
          <input v-model="createForm.startTime" type="time" required />
        </label>
        <label>
          トラック
          <input v-model="createForm.track" type="text" required />
        </label>
        <label>
          定員
          <input v-model="createForm.capacity" type="number" min="1" required />
        </label>
        <button type="submit" :disabled="!adminToken">セッション作成</button>
      </form>

      <table v-if="adminSessions.length > 0">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">開始時刻</th>
            <th scope="col">トラック</th>
            <th scope="col">タイトル</th>
            <th scope="col">定員</th>
            <th scope="col">予約数</th>
            <th scope="col">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in adminSessions" :key="session.sessionId">
            <td>{{ session.sessionId }}</td>
            <td>{{ session.startTime }}</td>
            <td>{{ session.track }}</td>
            <td>{{ session.title }}</td>
            <td>{{ session.capacity }}</td>
            <td>{{ session.reservedCount }}</td>
            <td>
              <button type="button" :disabled="!adminToken" @click="startEditSession(session)">
                編集
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <form v-if="editForm.sessionId" @submit.prevent="updateAdminSession">
        <h3>編集: {{ editForm.sessionId }}</h3>
        <label>
          タイトル
          <input v-model="editForm.title" type="text" required />
        </label>
        <label>
          開始時刻
          <input v-model="editForm.startTime" type="time" required />
        </label>
        <label>
          トラック
          <input v-model="editForm.track" type="text" required />
        </label>
        <label>
          定員
          <input v-model="editForm.capacity" type="number" min="1" required />
        </label>
        <button type="submit" :disabled="!adminToken">更新</button>
        <button type="button" @click="clearEditForm">キャンセル</button>
      </form>
    </section>

    <OperatorSessionTable
      :has-token="hasToken"
      :sessions="operatorSessionRows"
      @refresh="loadSessions"
      @reserve="reserveSession"
    />

    <OperatorReservationPanel
      :has-token="hasToken"
      :reservations="reservations"
      :registered="registered"
      :registration-status-loaded="registrationStatusLoaded"
      @refresh="loadReservations"
      @reserve-keynote="reserveKeynote"
      @cancel="cancelReservation"
    />

    <section>
      <h2>マイページ</h2>
      <p v-if="!token">マイページはログイン中ユーザーのみ表示できます。</p>
      <template v-else>
        <button type="button" @click="loadMyPage">マイページを更新</button>
        <p v-if="myPageLoaded && myPageReservations.length === 0">予約はありません。</p>
        <ul v-else>
          <li v-for="reservation in myPageReservations" :key="`mypage-${reservation}`">
            {{ reservation }}
          </li>
        </ul>
        <img
          v-if="myPageQrCodePayload && receptionQrCodeImageUrl"
          :src="receptionQrCodeImageUrl"
          :data-qr-payload="myPageQrCodePayload"
          alt="受付用QRコード"
          width="180"
          height="180"
        />
        <p v-if="myPageQrCodePayload && receptionQrCodeImageUrl">受付用QRコードを表示中</p>
        <p v-else-if="myPageQrCodePayload && qrCodeGenerationStatus === 'error'">
          受付用QRコードを生成できませんでした。再読み込みしてください。
        </p>
        <p v-else-if="myPageQrCodePayload">受付用QRコードを生成中です...</p>
      </template>
    </section>

    <OperatorCheckInPanel
      :has-token="hasToken"
      :qr-code-payload="checkInQrCodePayload"
      :selected-session-id="selectedCheckInSessionId"
      :session-options="checkInSessionOptions"
      :history="checkInHistoryRows"
      :history-loaded="checkInHistoryLoaded"
      :result-message="checkInResultMessage"
      :result-tone="checkInResultTone"
      @update:qr-code-payload="updateCheckInQrCodePayload"
      @update:selected-session-id="updateSelectedCheckInSessionId"
      @check-in-event="checkInEvent"
      @check-in-session="checkInSession"
      @refresh-history="loadCheckInHistory"
    />
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import OperatorCheckInPanel, {
  type OperatorCheckInHistoryRow,
  type OperatorCheckInSessionOption,
} from '../components/operator/OperatorCheckInPanel.vue';
import OperatorReservationPanel from '../components/operator/OperatorReservationPanel.vue';
import OperatorSessionTable, {
  type OperatorSessionRow,
} from '../components/operator/OperatorSessionTable.vue';
import { useReservationApp } from '../composables/useReservationApp';

const {
  token,
  adminToken,
  guestId,
  sessions,
  adminSessions,
  reservations,
  myPageReservations,
  myPageQrCodePayload,
  myPageLoaded,
  registered,
  registrationStatusLoaded,
  errorMessage,
  infoMessage,
  createForm,
  editForm,
  checkInQrCodePayload,
  selectedCheckInSessionId,
  checkIns,
  checkInHistoryLoaded,
  checkInResultMessage,
  checkInResultTone,
  receptionQrCodeImageUrl,
  qrCodeGenerationStatus,
  availabilityStatusLabel,
  isSessionReserved,
  checkInTypeLabel,
  formatCheckInTime,
  clearEditForm,
  loginAsGuest,
  loadSessions,
  loadReservations,
  loadMyPage,
  loadAdminSessions,
  reserveKeynote,
  reserveSession,
  cancelReservation,
  downloadReservationCsv,
  downloadSessionCheckInCsv,
  createAdminSession,
  startEditSession,
  updateAdminSession,
  checkInEvent,
  checkInSession,
  loadCheckInHistory,
  loadOperatorBootstrap,
} = useReservationApp();

const hasToken = computed<boolean>(() => token.value !== null);

const operatorSessionRows = computed<OperatorSessionRow[]>(() =>
  sessions.value.map((session) => ({
    sessionId: session.sessionId,
    title: session.title,
    startTime: session.startTime,
    track: session.track,
    availabilityStatusLabel: availabilityStatusLabel(session.availabilityStatus),
    reserved: isSessionReserved(session.sessionId),
    unavailable: session.availabilityStatus === 'FULL',
  })),
);

const checkInSessionOptions = computed<OperatorCheckInSessionOption[]>(() =>
  sessions.value.map((session) => ({
    sessionId: session.sessionId,
    title: session.title,
    startTime: session.startTime,
  })),
);

const checkInHistoryRows = computed<OperatorCheckInHistoryRow[]>(() =>
  checkIns.value.map((entry) => ({
    guestId: entry.guestId,
    checkInType: entry.checkInType,
    checkInTypeLabel: checkInTypeLabel(entry.checkInType),
    sessionId: entry.sessionId,
    checkedInAtLabel: formatCheckInTime(entry.checkedInAt),
  })),
);

const updateCheckInQrCodePayload = (value: string): void => {
  checkInQrCodePayload.value = value;
};

const updateSelectedCheckInSessionId = (value: string): void => {
  selectedCheckInSessionId.value = value;
};

onMounted(() => {
  void loadOperatorBootstrap();
});
</script>
