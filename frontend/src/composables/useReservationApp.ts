import { ref, watch } from 'vue';
import { toDataURL } from 'qrcode';

export type GuestLoginResponse = {
  accessToken: string;
  guestId: string;
  tokenType: string;
};

export type ReservationResponse = {
  guestId: string;
  reservations: string[];
  registered?: boolean;
};

export type MyPageResponse = {
  guestId: string;
  reservations: string[];
  receptionQrCodePayload: string;
};

export type SessionAvailabilityStatus = 'OPEN' | 'FEW_LEFT' | 'FULL';

export type SessionSummary = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  availabilityStatus: SessionAvailabilityStatus;
};

type SessionSummaryResponse = {
  sessions: SessionSummary[];
};

export type AdminSession = {
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

export type CheckInType = 'EVENT' | 'SESSION';
export type QrCodeGenerationStatus = 'idle' | 'generating' | 'ready' | 'error';
export type CheckInResultTone = 'success' | 'error' | '';

type CheckInResponse = {
  guestId: string;
  checkInType: CheckInType;
  sessionId: string | null;
  duplicate: boolean;
  checkedInAt: string;
};

export type CheckInHistoryEntry = {
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

export type AdminForm = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  capacity: string;
};

const API_BASE_URL = 'http://127.0.0.1:8080';
const QR_CODE_GENERATION_ERROR_MESSAGE = '受付QRコードの生成に失敗しました。';

const createDefaultCreateForm = (): AdminForm => ({
  sessionId: '',
  title: '',
  startTime: '16:30',
  track: '',
  capacity: '200',
});

const createDefaultEditForm = (): AdminForm => ({
  sessionId: '',
  title: '',
  startTime: '',
  track: '',
  capacity: '1',
});

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

export const useReservationApp = () => {
  const token = ref<string | null>(globalThis.sessionStorage.getItem('guestAccessToken'));
  const adminToken = ref<string>(globalThis.sessionStorage.getItem('adminAccessToken') ?? '');
  const guestId = ref<string>(globalThis.sessionStorage.getItem('guestId') ?? '');

  const sessions = ref<SessionSummary[]>([]);
  const adminSessions = ref<AdminSession[]>([]);
  const reservations = ref<string[]>([]);
  const myPageReservations = ref<string[]>([]);
  const myPageQrCodePayload = ref<string>('');
  const myPageQrRefreshTrigger = ref<number>(0);
  const myPageLoaded = ref<boolean>(false);
  const registered = ref<boolean>(false);
  const registrationStatusLoaded = ref<boolean>(false);

  const errorMessage = ref<string>('');
  const infoMessage = ref<string>('');

  const createForm = ref<AdminForm>(createDefaultCreateForm());
  const editForm = ref<AdminForm>(createDefaultEditForm());

  const checkInQrCodePayload = ref<string>('');
  const selectedCheckInSessionId = ref<string>('');
  const checkIns = ref<CheckInHistoryEntry[]>([]);
  const checkInHistoryLoaded = ref<boolean>(false);
  const checkInResultMessage = ref<string>('');
  const checkInResultTone = ref<CheckInResultTone>('');

  const receptionQrCodeImageUrl = ref<string>('');
  const qrCodeGenerationStatus = ref<QrCodeGenerationStatus>('idle');
  let qrGenerationRequestId = 0;

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

  const clearEditForm = (): void => {
    editForm.value = createDefaultEditForm();
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
    myPageQrRefreshTrigger.value += 1;
    myPageLoaded.value = true;
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

    createForm.value = createDefaultCreateForm();

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

  const checkInEvent = async (): Promise<void> => {
    if (!token.value) {
      return;
    }

    errorMessage.value = '';
    infoMessage.value = '';
    checkInResultMessage.value = '';
    checkInResultTone.value = '';
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
      checkInResultMessage.value =
        (await readErrorMessage(response)) ?? 'イベント受付チェックインに失敗しました。';
      checkInResultTone.value = 'error';
      return;
    }

    const data = (await response.json()) as CheckInResponse;
    checkInResultMessage.value = data.duplicate
      ? `${data.guestId} は既にイベント受付済みです。`
      : `${data.guestId} のイベント受付チェックインを記録しました。`;
    checkInResultTone.value = 'success';

    await loadCheckInHistory();
  };

  const checkInSession = async (): Promise<void> => {
    if (!token.value || selectedCheckInSessionId.value.length === 0) {
      return;
    }

    errorMessage.value = '';
    infoMessage.value = '';
    checkInResultMessage.value = '';
    checkInResultTone.value = '';
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
      checkInResultMessage.value =
        (await readErrorMessage(response)) ?? 'セッションチェックインに失敗しました。';
      checkInResultTone.value = 'error';
      return;
    }

    const data = (await response.json()) as CheckInResponse;
    const checkInTarget = data.sessionId ?? selectedCheckInSessionId.value;
    checkInResultMessage.value = data.duplicate
      ? `${data.guestId} は ${checkInTarget} で既にチェックイン済みです。`
      : `${data.guestId} の ${checkInTarget} チェックインを記録しました。`;
    checkInResultTone.value = 'success';

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
      checkInResultMessage.value = 'チェックイン履歴の取得に失敗しました。';
      checkInResultTone.value = 'error';
      return;
    }

    const data = (await response.json()) as CheckInHistoryResponse;
    checkIns.value = data.checkIns;
    checkInHistoryLoaded.value = true;
    if (checkInResultTone.value === 'error') {
      checkInResultMessage.value = '';
      checkInResultTone.value = '';
    }
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
    checkInResultTone.value = '';

    globalThis.sessionStorage.setItem('guestAccessToken', data.accessToken);
    globalThis.sessionStorage.setItem('guestId', data.guestId);

    await Promise.all([loadSessions(), loadReservations(), loadMyPage(), loadCheckInHistory()]);
  };

  const loadParticipantBootstrap = async (): Promise<void> => {
    if (!token.value) {
      return;
    }
    await Promise.all([loadSessions(), loadReservations(), loadMyPage()]);
  };

  const loadOperatorBootstrap = async (): Promise<void> => {
    if (token.value) {
      await Promise.all([loadSessions(), loadReservations(), loadMyPage(), loadCheckInHistory()]);
    }
    if (adminToken.value) {
      await loadAdminSessions();
    }
  };

  const endAdminSession = (): void => {
    adminToken.value = '';
    adminSessions.value = [];
    clearEditForm();
    infoMessage.value = '管理者セッションを終了しました。';
  };

  watch(adminToken, (newValue) => {
    if (newValue.trim().length === 0) {
      globalThis.sessionStorage.removeItem('adminAccessToken');
      return;
    }
    globalThis.sessionStorage.setItem('adminAccessToken', newValue);
  });

  watch(
    [myPageQrCodePayload, myPageQrRefreshTrigger],
    async ([payload]: [string, number]) => {
      const currentRequestId = ++qrGenerationRequestId;
      receptionQrCodeImageUrl.value = '';

      if (!payload) {
        qrCodeGenerationStatus.value = 'idle';
        if (errorMessage.value === QR_CODE_GENERATION_ERROR_MESSAGE) {
          errorMessage.value = '';
        }
        return;
      }

      qrCodeGenerationStatus.value = 'generating';

      try {
        const generatedQrCodeImageUrl = await toDataURL(payload, {
          width: 180,
          margin: 1,
        });
        if (currentRequestId !== qrGenerationRequestId || payload !== myPageQrCodePayload.value) {
          return;
        }
        receptionQrCodeImageUrl.value = generatedQrCodeImageUrl;
        qrCodeGenerationStatus.value = 'ready';
        if (errorMessage.value === QR_CODE_GENERATION_ERROR_MESSAGE) {
          errorMessage.value = '';
        }
      } catch {
        if (currentRequestId !== qrGenerationRequestId || payload !== myPageQrCodePayload.value) {
          return;
        }
        receptionQrCodeImageUrl.value = '';
        qrCodeGenerationStatus.value = 'error';
        errorMessage.value = QR_CODE_GENERATION_ERROR_MESSAGE;
      }
    },
    { immediate: true },
  );

  return {
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
    endAdminSession,
    checkInEvent,
    checkInSession,
    loadCheckInHistory,
    loadParticipantBootstrap,
    loadOperatorBootstrap,
  };
};
