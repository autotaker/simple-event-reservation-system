import { expect, test } from '@playwright/test';
import { installReservationMock } from './support/reservation-mock';
import { clearGuestSession, loginAsGuest, sessionRowByTitle } from './support/ui';

test.describe('US-04 予約制約を満たしてセッション予約できる', () => {
  test('通常セッション予約導線で制約違反時のエラーメッセージを確認できる', async ({ page }) => {
    await installReservationMock(page);

    await clearGuestSession(page);
    await loginAsGuest(page, 'guest-test');

    const session1Row = sessionRowByTitle(page, 'Session 1');
    await session1Row.getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('Session 1 を予約しました。')).toBeVisible();
    await expect(session1Row.getByRole('button', { name: '予約済み' })).toBeVisible();

    const session2Row = sessionRowByTitle(page, 'Session 2');
    await session2Row.getByRole('button', { name: '予約する' }).click();
    await expect(
      page.getByText('同じ時間帯のセッションは1つまでしか予約できません。'),
    ).toBeVisible();

    await sessionRowByTitle(page, 'Session 4').getByRole('button', { name: '予約する' }).click();
    await sessionRowByTitle(page, 'Session 7').getByRole('button', { name: '予約する' }).click();
    await sessionRowByTitle(page, 'Session 10').getByRole('button', { name: '予約する' }).click();
    await sessionRowByTitle(page, 'Session 13').getByRole('button', { name: '予約する' }).click();
    await sessionRowByTitle(page, 'Session 15').getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('通常セッションは最大5件までしか予約できません。')).toBeVisible();
  });
});
