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
        <li v-for="reservation in reservations" :key="reservation">{{ reservation }}</li>
      </ul>
    </section>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';

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

type ErrorResponse = {
  message?: string;
};

const API_BASE_URL = 'http://127.0.0.1:8080';
const token = ref<string | null>(globalThis.localStorage.getItem('guestAccessToken'));
const guestId = ref<string>(globalThis.localStorage.getItem('guestId') ?? '');
const sessions = ref<SessionSummary[]>([]);
const reservations = ref<string[]>([]);
const registered = ref<boolean>(false);
const registrationStatusLoaded = ref<boolean>(false);
const errorMessage = ref<string>('');
const infoMessage = ref<string>('');

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
  registered.value = false;
  registrationStatusLoaded.value = false;
  globalThis.localStorage.setItem('guestAccessToken', data.accessToken);
  globalThis.localStorage.setItem('guestId', data.guestId);

  await Promise.all([loadSessions(), loadReservations()]);
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
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
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
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  await loadSessions();
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
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  await loadSessions();
  infoMessage.value = `${title} を予約しました。`;
};

onMounted(() => {
  if (token.value) {
    void Promise.all([loadSessions(), loadReservations()]);
  }
});
</script>
