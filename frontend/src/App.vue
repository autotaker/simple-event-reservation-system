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
      <button type="button" :disabled="!token" @click="loadAdminSessions">管理一覧を取得</button>

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
        <button type="submit" :disabled="!token">セッション作成</button>
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
              <button type="button" :disabled="!token" @click="startEditSession(session)">編集</button>
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
        <button type="submit" :disabled="!token">更新</button>
        <button type="button" @click="clearEditForm">キャンセル</button>
      </form>
    </section>

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
          <button type="button" :disabled="!token" @click="cancelReservation(reservation)">キャンセル</button>
        </li>
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

type AdminSession = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  capacity: number;
  reservedCount: number;
};

type AdminSessionSummaryResponse = {
  sessions: AdminSession[];
};

type AdminSessionUpsertRequest = {
  title: string;
  startTime: string;
  track: string;
  capacity: number;
};

type ErrorResponse = {
  message?: string;
};

type AdminForm = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  capacity: string;
};

const API_BASE_URL = 'http://127.0.0.1:8080';
const token = ref<string | null>(globalThis.localStorage.getItem('guestAccessToken'));
const guestId = ref<string>(globalThis.localStorage.getItem('guestId') ?? '');
const sessions = ref<SessionSummary[]>([]);
const adminSessions = ref<AdminSession[]>([]);
const reservations = ref<string[]>([]);
const registered = ref<boolean>(false);
const registrationStatusLoaded = ref<boolean>(false);
const errorMessage = ref<string>('');
const infoMessage = ref<string>('');
const createForm = ref<AdminForm>({
  sessionId: '',
  title: '',
  startTime: '16:30',
  track: '',
  capacity: '200',
});
const editForm = ref<AdminForm>({
  sessionId: '',
  title: '',
  startTime: '',
  track: '',
  capacity: '1',
});

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

const buildAdminRequest = (form: AdminForm): AdminSessionUpsertRequest | null => {
  const parsedCapacity = Number(form.capacity);
  if (!Number.isInteger(parsedCapacity) || parsedCapacity <= 0) {
    errorMessage.value = '定員は1以上の整数で入力してください。';
    return null;
  }

  return {
    title: form.title.trim(),
    startTime: form.startTime,
    track: form.track.trim(),
    capacity: parsedCapacity,
  };
};

const clearEditForm = (): void => {
  editForm.value = {
    sessionId: '',
    title: '',
    startTime: '',
    track: '',
    capacity: '1',
  };
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
  adminSessions.value = [];
  reservations.value = [];
  registered.value = false;
  registrationStatusLoaded.value = false;
  globalThis.localStorage.setItem('guestAccessToken', data.accessToken);
  globalThis.localStorage.setItem('guestId', data.guestId);

  await Promise.all([loadSessions(), loadAdminSessions(), loadReservations()]);
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

const loadAdminSessions = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/admin/sessions`, {
    headers: {
      Authorization: `Bearer ${token.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value = '管理セッション一覧の取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as AdminSessionSummaryResponse;
  adminSessions.value = data.sessions;
};

const createAdminSession = async (): Promise<void> => {
  if (!token.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';

  const request = buildAdminRequest(createForm.value);
  if (!request) {
    return;
  }

  const response = await globalThis.fetch(`${API_BASE_URL}/api/admin/sessions`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token.value}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? 'セッション作成に失敗しました。';
    return;
  }

  createForm.value = {
    sessionId: '',
    title: '',
    startTime: '16:30',
    track: '',
    capacity: '200',
  };

  await Promise.all([loadAdminSessions(), loadSessions()]);
  infoMessage.value = 'セッションを作成しました。';
};

const startEditSession = (session: AdminSession): void => {
  editForm.value = {
    sessionId: session.sessionId,
    title: session.title,
    startTime: session.startTime,
    track: session.track,
    capacity: String(session.capacity),
  };
};

const updateAdminSession = async (): Promise<void> => {
  if (!token.value || !editForm.value.sessionId) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';

  const request = buildAdminRequest(editForm.value);
  if (!request) {
    return;
  }

  const response = await globalThis.fetch(
    `${API_BASE_URL}/api/admin/sessions/${encodeURIComponent(editForm.value.sessionId)}`,
    {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token.value}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    },
  );

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? 'セッション更新に失敗しました。';
    return;
  }

  await Promise.all([loadAdminSessions(), loadSessions()]);
  infoMessage.value = 'セッションを更新しました。';
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
    errorMessage.value = (await readErrorMessage(response)) ?? `${sessionId} のキャンセルに失敗しました。`;
    return;
  }

  const data = (await response.json()) as ReservationResponse;
  reservations.value = data.reservations;
  registered.value = data.registered ?? data.reservations.includes('keynote');
  registrationStatusLoaded.value = true;
  await loadSessions();
  infoMessage.value = `${sessionId} をキャンセルしました。`;
};

onMounted(() => {
  if (token.value) {
    void Promise.all([loadSessions(), loadAdminSessions(), loadReservations()]);
  }
});
</script>
