<template>
  <main>
    <h1>Event Reservation MVP</h1>
    <p>ゲストでログインしてキーノート予約を行えます。</p>

    <button type="button" @click="loginAsGuest">ゲストでログイン</button>
    <p v-if="guestId">ログイン中: {{ guestId }}</p>
    <p v-if="errorMessage">{{ errorMessage }}</p>
    <p v-if="infoMessage">{{ infoMessage }}</p>

    <section>
      <h2>セッション一覧</h2>
      <button type="button" :disabled="!token" @click="loadSessions">セッション一覧を取得</button>
      <p v-if="token && sessions.length === 0">セッション一覧は未取得です。</p>
      <table v-else-if="sessions.length > 0">
        <thead>
          <tr>
            <th scope="col">開始時刻</th>
            <th scope="col">トラック</th>
            <th scope="col">セッション</th>
            <th scope="col">残席ステータス</th>
            <th scope="col">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in sessions" :key="session.sessionId">
            <td>{{ session.startTime }}</td>
            <td>{{ session.track }}</td>
            <td>{{ session.title }}</td>
            <td>{{ availabilityStatusLabel(session.availabilityStatus) }}</td>
            <td>
              <button
                type="button"
                :disabled="
                  !token ||
                  session.availabilityStatus === 'FULL' ||
                  isSessionReserved(session.sessionId)
                "
                @click="reserveSession(session.sessionId, session.title)"
              >
                {{ isSessionReserved(session.sessionId) ? '予約済み' : '予約する' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>

    <section>
      <h2>予約</h2>
      <button type="button" :disabled="!token" @click="loadReservations">予約一覧を取得</button>
      <button type="button" :disabled="!token" @click="reserveKeynote">キーノートを予約</button>
      <p v-if="registered">参加登録: 完了</p>
      <p v-else-if="token && registrationStatusLoaded">参加登録: 未完了</p>

      <ul>
        <li v-for="reservation in reservations" :key="reservation">
          {{ reservation }}
          <button type="button" :disabled="!token" @click="cancelReservation(reservation)">
            キャンセル
          </button>
        </li>
      </ul>
    </section>

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
          v-if="myPageQrCodePayload"
          :src="receptionQrCodeImageUrl"
          alt="受付用QRコード"
          width="180"
          height="180"
        />
        <p v-if="myPageQrCodePayload">受付用QRコードを表示中</p>
      </template>
    </section>

    <section>
      <h2>運営チェックイン</h2>
      <p v-if="!token">運営チェックインはログイン中ユーザーのみ実行できます。</p>
      <template v-else>
        <label for="checkin-qr-payload">QR payload</label>
        <textarea
          id="checkin-qr-payload"
          v-model="checkInQrCodePayload"
          rows="3"
          cols="60"
          placeholder="event-reservation://checkin?guestId=...&reservations=..."
        />
        <div>
          <button type="button" @click="checkInEvent">イベント受付をチェックイン</button>
        </div>
        <div>
          <label for="checkin-session-id">セッション受付</label>
          <select id="checkin-session-id" v-model="selectedCheckInSessionId">
            <option value="">選択してください</option>
            <option v-for="session in sessions" :key="`checkin-${session.sessionId}`" :value="session.sessionId">
              {{ session.startTime }} {{ session.title }}
            </option>
          </select>
          <button
            type="button"
            :disabled="selectedCheckInSessionId.length === 0"
            @click="checkInSession"
          >
            セッションをチェックイン
          </button>
        </div>
        <button type="button" @click="loadCheckInHistory">チェックイン履歴を更新</button>
        <p v-if="checkInResultMessage">{{ checkInResultMessage }}</p>
        <p v-if="checkInHistoryLoaded && checkIns.length === 0">チェックイン履歴はありません。</p>
        <table v-else-if="checkIns.length > 0">
          <thead>
            <tr>
              <th scope="col">チェックイン種別</th>
              <th scope="col">ゲストID</th>
              <th scope="col">セッションID</th>
              <th scope="col">時刻</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="checkIn in checkIns" :key="`${checkIn.checkInType}-${checkIn.sessionId ?? 'event'}-${checkIn.guestId}`">
              <td>{{ checkInTypeLabel(checkIn.checkInType) }}</td>
              <td>{{ checkIn.guestId }}</td>
              <td>{{ checkIn.sessionId ?? '-' }}</td>
              <td>{{ formatCheckInTime(checkIn.checkedInAt) }}</td>
            </tr>
          </tbody>
        </table>
      </template>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';

type GuestLoginResponse = {
  accessToken: string;
  guestId: string;
  tokenType: string;
};

type ReservationResponse = {
  guestId: string;
  reservations: string[];
  registered?: boolean;
};

type MyPageResponse = {
  guestId: string;
  reservations: string[];
  receptionQrCodePayload: string;
};

type SessionAvailabilityStatus = 'OPEN' | 'FEW_LEFT' | 'FULL';

type SessionSummary = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  availabilityStatus: SessionAvailabilityStatus;
};

type SessionSummaryResponse = {
  sessions: SessionSummary[];
};

type CheckInType = 'EVENT' | 'SESSION';

type CheckInResponse = {
  guestId: string;
  checkInType: CheckInType;
  sessionId: string | null;
  duplicate: boolean;
  checkedInAt: string;
};

type CheckInHistoryEntry = {
  guestId: string;
  checkInType: CheckInType;
  sessionId: string | null;
  checkedInAt: string;
};

type CheckInHistoryResponse = {
  checkIns: CheckInHistoryEntry[];
};

type ErrorResponse = {
  message?: string;
};

const API_BASE_URL = 'http://127.0.0.1:8080';
const token = ref<string | null>(globalThis.localStorage.getItem('guestAccessToken'));
const guestId = ref<string>(globalThis.localStorage.getItem('guestId') ?? '');
const sessions = ref<SessionSummary[]>([]);
const reservations = ref<string[]>([]);
const myPageReservations = ref<string[]>([]);
const myPageQrCodePayload = ref<string>('');
const myPageLoaded = ref<boolean>(false);
const registered = ref<boolean>(false);
const registrationStatusLoaded = ref<boolean>(false);
const errorMessage = ref<string>('');
const infoMessage = ref<string>('');
const checkInQrCodePayload = ref<string>('');
const selectedCheckInSessionId = ref<string>('');
const checkIns = ref<CheckInHistoryEntry[]>([]);
const checkInHistoryLoaded = ref<boolean>(false);
const checkInResultMessage = ref<string>('');

const availabilityStatusLabel = (status: SessionAvailabilityStatus): string => {
  if (status === 'OPEN') {
    return '受付中';
  }
  if (status === 'FEW_LEFT') {
    return '残りわずか';
  }
  return '満席';
};

const isSessionReserved = (sessionId: string): boolean => reservations.value.includes(sessionId);
const checkInTypeLabel = (checkInType: CheckInType): string =>
  checkInType === 'EVENT' ? 'イベント受付' : 'セッション受付';
const formatCheckInTime = (checkedInAt: string): string =>
  new Date(checkedInAt).toLocaleString('ja-JP', { hour12: false });
const receptionQrCodeImageUrl = computed(
  () =>
    `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(myPageQrCodePayload.value)}`,
);

const readErrorMessage = async (response: globalThis.Response): Promise<string | null> => {
  try {
    const payload = (await response.json()) as ErrorResponse;
    if (payload.message && payload.message.trim().length > 0) {
      return payload.message;
    }
  } catch {
    return null;
  }
  return null;
};

const loginAsGuest = async (): Promise<void> => {
  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/auth/guest`, {
    method: 'POST',
  });

  if (!response.ok) {
    errorMessage.value = 'ゲストログインに失敗しました。';
    return;
  }

  const data = (await response.json()) as GuestLoginResponse;
  token.value = data.accessToken;
  guestId.value = data.guestId;
  sessions.value = [];
  reservations.value = [];
  myPageReservations.value = [];
  myPageQrCodePayload.value = '';
  myPageLoaded.value = false;
  registered.value = false;
  registrationStatusLoaded.value = false;
  checkInQrCodePayload.value = '';
  selectedCheckInSessionId.value = '';
  checkIns.value = [];
  checkInHistoryLoaded.value = false;
  checkInResultMessage.value = '';
  globalThis.localStorage.setItem('guestAccessToken', data.accessToken);
  globalThis.localStorage.setItem('guestId', data.guestId);

  await Promise.all([loadSessions(), loadReservations(), loadMyPage(), loadCheckInHistory()]);
};

const loadSessions = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/reservations/sessions`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value = 'セッション一覧の取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as SessionSummaryResponse;
  sessions.value = data.sessions;
};

const loadReservations = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/reservations`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value = '予約取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  myPageReservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
};

const loadMyPage = async (): Promise<void> => {
  if (!token.value) {
    myPageLoaded.value = false;
    myPageReservations.value = [];
    myPageQrCodePayload.value = '';
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/reservations/mypage`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    myPageLoaded.value = false;
    myPageReservations.value = [];
    myPageQrCodePayload.value = '';
    errorMessage.value = 'マイページ情報の取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as MyPageResponse;
  myPageReservations.value = data.reservations;
  myPageQrCodePayload.value = data.receptionQrCodePayload;
  myPageLoaded.value = true;
};

const reserveKeynote = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/reservations/keynote`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? 'キーノート予約に失敗しました。';
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  myPageReservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  await Promise.all([loadSessions(), loadMyPage()]);
  infoMessage.value = 'キーノートを予約しました。';
};

const reserveSession = async (sessionId: string, title: string): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(
    `${API_BASE_URL}/api/reservations/sessions/${encodeURIComponent(sessionId)}`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    },
  );

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? `${title} の予約に失敗しました。`;
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  myPageReservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  await Promise.all([loadSessions(), loadMyPage()]);
  infoMessage.value = `${title} を予約しました。`;
};

const cancelReservation = async (sessionId: string): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(
    `${API_BASE_URL}/api/reservations/sessions/${encodeURIComponent(sessionId)}`,
    {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token.value}`,
      },
    },
  );

  if (!response.ok) {
    errorMessage.value =
      (await readErrorMessage(response)) ?? `${sessionId} のキャンセルに失敗しました。`;
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  myPageReservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  await Promise.all([loadSessions(), loadMyPage()]);
  infoMessage.value = `${sessionId} をキャンセルしました。`;
};

const checkInEvent = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  checkInResultMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/checkins/event`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token.value}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      qrCodePayload: checkInQrCodePayload.value,
    }),
  });

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? 'イベント受付チェックインに失敗しました。';
    return;
  }

  const data = (await response.json()) as CheckInResponse;
  checkInResultMessage.value = data.duplicate
    ? `${data.guestId} は既にイベント受付済みです。`
    : `${data.guestId} のイベント受付チェックインを記録しました。`;
  await loadCheckInHistory();
};

const checkInSession = async (): Promise<void> => {
  if (!token.value || selectedCheckInSessionId.value.length === 0) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  checkInResultMessage.value = '';
  const response = await globalThis.fetch(
    `${API_BASE_URL}/api/checkins/sessions/${encodeURIComponent(selectedCheckInSessionId.value)}`,
    {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        qrCodePayload: checkInQrCodePayload.value,
      }),
    },
  );

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? 'セッションチェックインに失敗しました。';
    return;
  }

  const data = (await response.json()) as CheckInResponse;
  const checkInTarget = data.sessionId ?? selectedCheckInSessionId.value;
  checkInResultMessage.value = data.duplicate
    ? `${data.guestId} は ${checkInTarget} で既にチェックイン済みです。`
    : `${data.guestId} の ${checkInTarget} チェックインを記録しました。`;
  await loadCheckInHistory();
};

const loadCheckInHistory = async (): Promise<void> => {
  if (!token.value) {
    checkIns.value = [];
    checkInHistoryLoaded.value = false;
    return;
  }

  errorMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/checkins`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    checkIns.value = [];
    checkInHistoryLoaded.value = false;
    errorMessage.value = 'チェックイン履歴の取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as CheckInHistoryResponse;
  checkIns.value = data.checkIns;
  checkInHistoryLoaded.value = true;
};

onMounted(() => {
  if (token.value) {
    void Promise.all([loadSessions(), loadReservations(), loadMyPage(), loadCheckInHistory()]);
  }
});
</script>
