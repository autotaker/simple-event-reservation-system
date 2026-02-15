import { expect, test } from '@playwright/test';
import { readFile } from 'node:fs/promises';
import { clearGuestSession, loginAsGuest } from './support/ui';

const ADMIN_TOKEN = process.env.E2E_ADMIN_TOKEN ?? 'e2e-admin-token';

test.describe('US-09 予約状況をCSVでエクスポートできる', () => {
  test('未認証でCSVエクスポートAPIにアクセスすると401になる', async ({ request }) => {
    const reservationsResponse = await request.get('http://127.0.0.1:8080/api/admin/exports/reservations');
    expect(reservationsResponse.status()).toBe(401);

    const checkInsResponse = await request.get('http://127.0.0.1:8080/api/admin/exports/session-checkins');
    expect(checkInsResponse.status()).toBe(401);
  });

  test('運営は予約一覧とチェックインCSVをUTF-8で出力できる', async ({ page }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);

    await page.getByLabel('管理者トークン').fill(ADMIN_TOKEN);
    await expect(page.getByRole('button', { name: '予約一覧CSVを出力' })).toBeVisible();

    const reservationsDownloadPromise = page.waitForEvent('download');
    await page.getByRole('button', { name: '予約一覧CSVを出力' }).click();
    await expect(page.getByText('予約一覧CSVを出力しました。')).toBeVisible();
    const reservationsDownload = await reservationsDownloadPromise;
    expect(reservationsDownload.suggestedFilename()).toBe('reservations.csv');

    const reservationsPath = await reservationsDownload.path();
    expect(reservationsPath).toBeTruthy();
    const reservationsContent = await readFile(reservationsPath!, 'utf8');
    expect(reservationsContent).toContain('guestId,sessionId,sessionTitle,startTime,track');

    const checkInsDownloadPromise = page.waitForEvent('download');
    await page.getByRole('button', { name: 'チェックインCSVを出力' }).click();
    await expect(page.getByText('チェックインCSVを出力しました。')).toBeVisible();
    const checkInsDownload = await checkInsDownloadPromise;
    expect(checkInsDownload.suggestedFilename()).toBe('session-checkins.csv');

    const checkInsPath = await checkInsDownload.path();
    expect(checkInsPath).toBeTruthy();
    const checkInsContent = await readFile(checkInsPath!, 'utf8');
    expect(checkInsContent).toContain('sessionId,sessionTitle,startTime,track,guestId,checkedIn,checkedInAt');
  });
});
