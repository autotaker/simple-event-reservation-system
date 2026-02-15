import type { Page } from '@playwright/test';

type SessionCatalogEntry = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
};

const TEST_GUEST_ID = 'guest-test';
const KEYNOTE_SESSION = 'keynote';

export const installReservationMock = async (page: Page): Promise<void> => {
  const reservationsByGuest = new Map<string, Set<string>>();
  const reservationsBySession = new Map<string, Set<string>>();
  const sessionCatalog = createSessionCatalog();

  const getGuestReservations = (guestId: string): Set<string> => {
    const current = reservationsByGuest.get(guestId);
    if (current) {
      return current;
    }
    const created = new Set<string>();
    reservationsByGuest.set(guestId, created);
    return created;
  };

  const getSessionReservations = (sessionId: string): Set<string> => {
    const current = reservationsBySession.get(sessionId);
    if (current) {
      return current;
    }
    const created = new Set<string>();
    reservationsBySession.set(sessionId, created);
    return created;
  };

  const availabilityStatus = (sessionId: string): string => {
    const capacity = sessionId === KEYNOTE_SESSION ? 700 : 200;
    const reserved = getSessionReservations(sessionId).size;
    const remaining = Math.max(0, capacity - reserved);
    if (remaining <= 0) {
      return 'FULL';
    }
    if (remaining < 20) {
      return 'FEW_LEFT';
    }
    return 'OPEN';
  };

  await page.route('**/api/auth/guest', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        accessToken: 'test-token',
        tokenType: 'Bearer',
        guestId: TEST_GUEST_ID,
      }),
    });
  });

  await page.route('**/api/reservations/sessions', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        sessions: sessionCatalog.map((session) => ({
          ...session,
          availabilityStatus: availabilityStatus(session.sessionId),
        })),
      }),
    });
  });

  await page.route('**/api/reservations', async (route) => {
    if (route.request().method() !== 'GET') {
      await route.fallback();
      return;
    }
    const guestReservations = Array.from(getGuestReservations(TEST_GUEST_ID));
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        guestId: TEST_GUEST_ID,
        reservations: guestReservations,
        registered: guestReservations.includes(KEYNOTE_SESSION),
      }),
    });
  });

  await page.route('**/api/reservations/sessions/*', async (route) => {
    const sessionId = route.request().url().split('/').pop() ?? '';
    const decodedSessionId = decodeURIComponent(sessionId);
    const session = sessionCatalog.find((item) => item.sessionId === decodedSessionId);

    if (!session) {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({ message: '指定されたセッションは存在しません。' }),
      });
      return;
    }

    const guestReservations = getGuestReservations(TEST_GUEST_ID);
    if (guestReservations.has(decodedSessionId)) {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          guestId: TEST_GUEST_ID,
          reservations: Array.from(guestReservations),
          registered: guestReservations.has(KEYNOTE_SESSION),
        }),
      });
      return;
    }

    const regularCount = Array.from(guestReservations).filter(
      (reservation) => reservation !== KEYNOTE_SESSION,
    ).length;
    if (decodedSessionId !== KEYNOTE_SESSION && regularCount >= 5) {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({ message: '通常セッションは最大5件までしか予約できません。' }),
      });
      return;
    }

    const conflict = Array.from(guestReservations)
      .map((reservation) => sessionCatalog.find((item) => item.sessionId === reservation))
      .some((reservedSession) => reservedSession && reservedSession.startTime === session.startTime);
    if (conflict) {
      await route.fulfill({
        status: 400,
        contentType: 'application/json',
        body: JSON.stringify({ message: '同じ時間帯のセッションは1つまでしか予約できません。' }),
      });
      return;
    }

    const sessionReservations = getSessionReservations(decodedSessionId);
    const capacity = decodedSessionId === KEYNOTE_SESSION ? 700 : 200;
    if (sessionReservations.size >= capacity) {
      await route.fulfill({
        status: 409,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'セッションは満席です。' }),
      });
      return;
    }

    sessionReservations.add(TEST_GUEST_ID);
    guestReservations.add(decodedSessionId);
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        guestId: TEST_GUEST_ID,
        reservations: Array.from(guestReservations),
        registered: guestReservations.has(KEYNOTE_SESSION),
      }),
    });
  });

  await page.route('**/api/reservations/keynote', async (route) => {
    if (route.request().method() !== 'POST') {
      await route.fallback();
      return;
    }

    const guestReservations = getGuestReservations(TEST_GUEST_ID);
    if (!guestReservations.has(KEYNOTE_SESSION)) {
      const keynoteReservations = getSessionReservations(KEYNOTE_SESSION);
      keynoteReservations.add(TEST_GUEST_ID);
      guestReservations.add(KEYNOTE_SESSION);
    }

    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({
        guestId: TEST_GUEST_ID,
        reservations: Array.from(guestReservations),
        registered: true,
      }),
    });
  });
};

const createSessionCatalog = (): SessionCatalogEntry[] => {
  const sessions: SessionCatalogEntry[] = [
    { sessionId: KEYNOTE_SESSION, title: 'Opening Keynote', startTime: '09:00', track: 'Keynote' },
  ];

  const startTimes = ['10:30', '11:30', '13:30', '14:30', '15:30'];
  const tracks = ['Track A', 'Track B', 'Track C'];
  let sequence = 1;
  for (const startTime of startTimes) {
    for (const track of tracks) {
      sessions.push({
        sessionId: `session-${sequence}`,
        title: `Session ${sequence}`,
        startTime,
        track,
      });
      sequence += 1;
    }
  }

  return sessions;
};
