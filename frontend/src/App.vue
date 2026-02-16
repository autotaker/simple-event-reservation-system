<template>
  <main
    v-if="isParticipantRoute"
    class="participant-portal"
    :class="`participant-portal--${participantMode}`"
  >
    <header class="participant-topbar">
      <div>
        <p class="participant-topbar__event">Tokyo Product Summit 2026</p>
        <h1 class="participant-topbar__title">{{ participantDateLabel }}</h1>
      </div>
      <div class="participant-topbar__guest">
        <span class="participant-topbar__badge">{{ guestId || 'Guest' }}</span>
        <button type="button" @click="loginAsGuest">ゲストでログイン</button>
      </div>
    </header>

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
            :disabled="!token || participantBusy"
            @click="runParticipantAction(loadSessions)"
          >
            更新
          </button>
        </header>
        <p v-if="token && sessions.length === 0">セッション一覧は未取得です。</p>
        <article
          v-for="session in sessions"
          :key="session.sessionId"
          class="participant-session-card"
          :class="{ 'participant-session-card--reserved': isSessionReserved(session.sessionId) }"
        >
          <p class="participant-session-card__meta">
            {{ session.startTime }} | {{ session.track }}
          </p>
          <h3 class="participant-session-card__title">{{ session.title }}</h3>
          <div class="participant-session-card__footer">
            <span
              :class="[
                'participant-session-card__seat',
                `participant-session-card__seat--${availabilitySeatTone(session.availabilityStatus)}`,
              ]"
            >
              {{ availabilityStatusLabel(session.availabilityStatus) }}
            </span>
            <button
              type="button"
              :disabled="
                !token ||
                participantBusy ||
                session.availabilityStatus === 'FULL' ||
                isSessionReserved(session.sessionId)
              "
              @click="runParticipantAction(() => reserveSession(session.sessionId, session.title))"
            >
              {{ isSessionReserved(session.sessionId) ? '予約済み' : '予約する' }}
            </button>
          </div>
        </article>
      </section>

      <aside class="participant-side">
        <section class="participant-panel">
          <header class="participant-panel__header">
            <h2>予約一覧</h2>
            <button
              type="button"
              :disabled="!token || participantBusy"
              @click="runParticipantAction(loadReservations)"
            >
              更新
            </button>
          </header>
          <div class="participant-panel__actions">
            <button
              type="button"
              :disabled="!token || participantBusy"
              @click="runParticipantAction(reserveKeynote)"
            >
              キーノートを予約
            </button>
          </div>
          <p v-if="registered">参加登録: 完了</p>
          <p v-else-if="token && registrationStatusLoaded">参加登録: 未完了</p>
          <ul v-if="participantReservationItems.length > 0" class="participant-reservation-list">
            <li v-for="item in participantReservationItems" :key="item.id">
              <p>{{ item.title }}</p>
              <span>{{ item.time }}</span>
              <button
                type="button"
                :disabled="!token || participantBusy"
                @click="runParticipantAction(() => cancelReservation(item.id))"
              >
                キャンセル
              </button>
            </li>
          </ul>
          <p v-else-if="token">予約はありません。</p>
        </section>

        <section class="participant-panel participant-panel--qr">
          <header class="participant-panel__header">
            <h2>マイページ</h2>
            <button
              type="button"
              :disabled="!token || participantBusy"
              @click="runParticipantAction(loadMyPage)"
            >
              更新
            </button>
          </header>
          <img
            v-if="myPageQrCodePayload"
            :src="receptionQrCodeImageUrl"
            alt="受付用QRコード"
            width="180"
            height="180"
          />
          <div v-else class="participant-panel__qr-placeholder" aria-hidden="true">QR</div>
          <p>
            {{
              myPageQrCodePayload
                ? '受付用QRコードを表示中'
                : 'ログイン後に受付QRコードが表示されます。'
            }}
          </p>
          <ul v-if="myPageReservations.length > 0" class="participant-mypage-list">
            <li v-for="reservation in myPageReservations" :key="`mypage-${reservation}`">
              {{ reservation }}
            </li>
          </ul>
        </section>
      </aside>
    </section>

    <p
      v-if="participantMode === 'loading'"
      class="participant-feedback participant-feedback--loading"
    >
      予約情報を更新中です...
    </p>
  </main>

  <main v-else>
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
            <option
              v-for="session in sessions"
              :key="`checkin-${session.sessionId}`"
              :value="session.sessionId"
            >
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
            <tr
              v-for="checkIn in checkIns"
              :key="`${checkIn.checkInType}-${checkIn.sessionId ?? 'event'}-${checkIn.guestId}`"
            >
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
import { computed, onMounted, ref, watch } from 'vue';

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

type AdminForm = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  capacity: string;
};

type ParticipantReservationItem = {
  id: string;
  title: string;
  time: string;
};

const API_BASE_URL = 'http://127.0.0.1:8080';
const token = ref<string | null>(globalThis.localStorage.getItem('guestAccessToken'));
const adminToken = ref<string>(globalThis.localStorage.getItem('adminAccessToken') ?? '');
const guestId = ref<string>(globalThis.localStorage.getItem('guestId') ?? '');
const sessions = ref<SessionSummary[]>([]);
const adminSessions = ref<AdminSession[]>([]);
const reservations = ref<string[]>([]);
const myPageReservations = ref<string[]>([]);
const myPageQrCodePayload = ref<string>('');
const myPageLoaded = ref<boolean>(false);
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
const checkInQrCodePayload = ref<string>('');
const selectedCheckInSessionId = ref<string>('');
const checkIns = ref<CheckInHistoryEntry[]>([]);
const checkInHistoryLoaded = ref<boolean>(false);
const checkInResultMessage = ref<string>('');
const participantBusy = ref<boolean>(false);
const isParticipantRoute = computed(() => globalThis.location.pathname.startsWith('/participant'));
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
const availabilitySeatTone = (status: SessionAvailabilityStatus): 'open' | 'few' | 'full' => {
  if (status === 'OPEN') {
    return 'open';
  }
  if (status === 'FEW_LEFT') {
    return 'few';
  }
  return 'full';
};
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

const runParticipantAction = async (action: () => Promise<void>): Promise<void> => {
  participantBusy.value = true;
  try {
    await action();
  } finally {
    participantBusy.value = false;
  }
};

const downloadAdminCsv = async (
  path: string,
  filename: string,
  successMessage: string,
): Promise<void> => {
  if (!adminToken.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}${path}`, {
    headers: {
      Authorization: `Bearer ${adminToken.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value = (await readErrorMessage(response)) ?? 'CSVの出力に失敗しました。';
    return;
  }

  const csvBody = await response.text();
  const blob = new globalThis.Blob([csvBody], { type: 'text/csv;charset=utf-8' });
  const url = globalThis.URL.createObjectURL(blob);
  const link = globalThis.document.createElement('a');
  link.href = url;
  link.download = filename;
  link.click();
  globalThis.URL.revokeObjectURL(url);
  infoMessage.value = successMessage;
};

const downloadReservationCsv = async (): Promise<void> =>
  downloadAdminCsv(
    '/api/admin/exports/reservations',
    'reservations.csv',
    '予約一覧CSVを出力しました。',
  );

const downloadSessionCheckInCsv = async (): Promise<void> =>
  downloadAdminCsv(
    '/api/admin/exports/session-checkins',
    'session-checkins.csv',
    'チェックインCSVを出力しました。',
  );

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

const loadAdminSessions = async (): Promise<void> => {
  if (!adminToken.value) {
    return;
  }

  errorMessage.value = '';
  infoMessage.value = '';
  const response = await globalThis.fetch(`${API_BASE_URL}/api/admin/sessions`, {
    headers: {
      Authorization: `Bearer ${adminToken.value}`,
    },
  });

  if (!response.ok) {
    errorMessage.value =
      (await readErrorMessage(response)) ?? '管理セッション一覧の取得に失敗しました。';
    return;
  }

  const data = (await response.json()) as AdminSessionSummaryResponse;
  adminSessions.value = data.sessions;
};

const createAdminSession = async (): Promise<void> => {
  if (!adminToken.value) {
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
      Authorization: `Bearer ${adminToken.value}`,
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
  if (!adminToken.value || !editForm.value.sessionId) {
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
        Authorization: `Bearer ${adminToken.value}`,
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

watch(adminToken, (newValue) => {
  globalThis.localStorage.setItem('adminAccessToken', newValue);
});

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
    errorMessage.value =
      (await readErrorMessage(response)) ?? 'イベント受付チェックインに失敗しました。';
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
    errorMessage.value =
      (await readErrorMessage(response)) ?? 'セッションチェックインに失敗しました。';
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
  if (!isParticipantRoute.value && adminToken.value) {
    void loadAdminSessions();
  }
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

.participant-topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 16px;
  border: 1px solid #cde8dd;
  background: linear-gradient(145deg, #effff7, #f7fffb);
}

.participant-topbar__event {
  margin: 0;
  font-size: 12px;
  color: #3e7360;
}

.participant-topbar__title {
  margin: 2px 0 0;
  font-size: 18px;
  font-weight: 700;
  color: #173a2c;
}

.participant-topbar__guest {
  display: flex;
  align-items: center;
  gap: 10px;
}

.participant-topbar__badge {
  padding: 6px 10px;
  border-radius: 999px;
  background: #163f30;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

.participant-topbar button {
  height: 34px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  background: #2b7a58;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
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

.participant-sessions h2,
.participant-panel h2 {
  margin: 0;
  font-size: 14px;
}

.participant-sessions__header button,
.participant-panel button {
  height: 30px;
  padding: 0 10px;
  border: none;
  border-radius: 8px;
  background: #315e96;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

.participant-session-card {
  display: grid;
  gap: 8px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #d7efe5;
  background: #ffffff;
}

.participant-session-card--reserved {
  background: #f4fbf8;
}

.participant-session-card__meta,
.participant-session-card__title {
  margin: 0;
}

.participant-session-card__meta {
  font-size: 12px;
  color: #4a7c67;
}

.participant-session-card__title {
  font-size: 15px;
  color: #1c3f31;
}

.participant-session-card__footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.participant-session-card__seat {
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.participant-session-card__seat--open {
  background: #d8fbe7;
  color: #18613e;
}

.participant-session-card__seat--few {
  background: #fff0cf;
  color: #925a00;
}

.participant-session-card__seat--full {
  background: #ffe1e1;
  color: #a82626;
}

.participant-session-card button {
  height: 32px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 700;
  background: #1f8e5f;
  color: #ffffff;
}

.participant-side {
  display: grid;
  gap: 10px;
  align-content: start;
}

.participant-panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #d9e8f4;
  background: #ffffff;
}

.participant-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.participant-panel__actions {
  display: flex;
}

.participant-reservation-list,
.participant-mypage-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

.participant-reservation-list li {
  display: grid;
  gap: 4px;
  padding: 8px;
  border-radius: 10px;
  background: #f8fbff;
}

.participant-reservation-list p,
.participant-reservation-list span {
  margin: 0;
}

.participant-reservation-list span {
  font-size: 11px;
  color: #4f657f;
}

.participant-panel--qr {
  justify-items: center;
  border: 1px solid #d8efe3;
}

.participant-panel--qr .participant-panel__header {
  width: 100%;
}

.participant-panel__qr-placeholder {
  width: 112px;
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  border-radius: 12px;
  border: 2px dashed #2a7758;
  font-size: 28px;
  font-weight: 700;
  color: #2a7758;
  background: repeating-linear-gradient(-45deg, #f5fff9, #f5fff9 7px, #ecfbf3 7px, #ecfbf3 14px);
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

.participant-session-card button:disabled,
.participant-panel button:disabled,
.participant-sessions__header button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
