import { expect, test } from '@playwright/test';

test.describe('US-04 予約制約を満たしてセッション予約できる', () => {
  test('通常セッション予約導線で制約違反時のエラーメッセージを確認できる', async ({ page }) => {
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
      const capacity = sessionId === 'keynote' ? 700 : 200;
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
          guestId: 'guest-test',
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
      const guestReservations = Array.from(getGuestReservations('guest-test'));
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          guestId: 'guest-test',
          reservations: guestReservations,
          registered: guestReservations.includes('keynote'),
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

      const guestReservations = getGuestReservations('guest-test');
      if (guestReservations.has(decodedSessionId)) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({
            guestId: 'guest-test',
            reservations: Array.from(guestReservations),
            registered: guestReservations.has('keynote'),
          }),
        });
        return;
      }

      const regularCount = Array.from(guestReservations).filter((reservation) => reservation !== 'keynote').length;
      if (decodedSessionId !== 'keynote' && regularCount >= 5) {
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
      const capacity = decodedSessionId === 'keynote' ? 700 : 200;
      if (sessionReservations.size >= capacity) {
        await route.fulfill({
          status: 409,
          contentType: 'application/json',
          body: JSON.stringify({ message: 'セッションは満席です。' }),
        });
        return;
      }

      sessionReservations.add('guest-test');
      guestReservations.add(decodedSessionId);
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({
          guestId: 'guest-test',
          reservations: Array.from(guestReservations),
          registered: guestReservations.has('keynote'),
        }),
      });
    });

    await page.goto('/');
    await page.evaluate(() => {
      localStorage.removeItem('guestAccessToken');
      localStorage.removeItem('guestId');
    });
    await page.reload();

    await page.getByRole('button', { name: 'ゲストでログイン' }).click();
    await expect(page.getByText('ログイン中: guest-test')).toBeVisible();

    const sessionRow = (title: string) =>
      page.locator('tbody tr').filter({ has: page.locator('td', { hasText: new RegExp(`^${title}$`) }) });

    const session1Row = sessionRow('Session 1');
    await session1Row.getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('Session 1 を予約しました。')).toBeVisible();
    await expect(session1Row.getByRole('button', { name: '予約済み' })).toBeVisible();

    const session2Row = sessionRow('Session 2');
    await session2Row.getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('同じ時間帯のセッションは1つまでしか予約できません。')).toBeVisible();

    await sessionRow('Session 4').getByRole('button', { name: '予約する' }).click();
    await sessionRow('Session 7').getByRole('button', { name: '予約する' }).click();
    await sessionRow('Session 10').getByRole('button', { name: '予約する' }).click();
    await sessionRow('Session 13').getByRole('button', { name: '予約する' }).click();
    await sessionRow('Session 15').getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('通常セッションは最大5件までしか予約できません。')).toBeVisible();
  });
});

const createSessionCatalog = (): Array<{ sessionId: string; title: string; startTime: string; track: string }> => {
  const sessions: Array<{ sessionId: string; title: string; startTime: string; track: string }> = [
    { sessionId: 'keynote', title: 'Opening Keynote', startTime: '09:00', track: 'Keynote' },
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
