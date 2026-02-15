import { expect, test } from '@playwright/test';
import { clearGuestSession, loginAsGuest, sessionRowByTitle } from './support/ui';

const ADMIN_TOKEN = 'e2e-admin-token';

test.describe('US-09 予約状況をCSVでエクスポートできる', () => {
  test('未認証でCSV出力APIにアクセスすると401になる', async ({ request }) => {
    const response = await request.get('http://127.0.0.1:8080/api/admin/exports/reservations');
    expect(response.status()).toBe(401);
  });

  test('運営は予約一覧CSVとチェックインCSVを出力できる', async ({ page }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);

    await page.getByRole('button', { name: 'キーノートを予約' }).click();
    await expect(page.getByText('キーノートを予約しました。')).toBeVisible();

    const session1Row = sessionRowByTitle(page, 'Session 1');
    await expect(session1Row).toBeVisible();
    await session1Row.getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('Session 1 を予約しました。')).toBeVisible();

    await page.getByLabel('管理者トークン').fill(ADMIN_TOKEN);

    const [reservationsDownload] = await Promise.all([
      page.waitForEvent('download'),
      page.getByRole('button', { name: '予約一覧CSVを出力' }).click(),
    ]);
    expect(reservationsDownload.suggestedFilename()).toBe('reservations.csv');
    await expect(page.getByText('予約一覧CSVを出力しました。')).toBeVisible();

    const [checkInsDownload] = await Promise.all([
      page.waitForEvent('download'),
      page.getByRole('button', { name: 'チェックインCSVを出力' }).click(),
    ]);
    expect(checkInsDownload.suggestedFilename()).toBe('session-checkins.csv');
    await expect(page.getByText('チェックインCSVを出力しました。')).toBeVisible();
  });
});
