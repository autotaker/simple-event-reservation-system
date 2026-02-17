import { expect, test } from '@playwright/test';
import { clearGuestSession, loginAsGuest, sessionRowByTitle } from './support/ui';

test.describe('US-07 運営がQRスキャンでチェックイン記録できる', () => {
  test('未認証でチェックイン履歴APIにアクセスすると401になる', async ({ request }) => {
    const response = await request.get('http://127.0.0.1:8080/api/checkins');
    expect(response.status()).toBe(401);
  });

  test('運営チェックイン導線でイベント・セッション受付と重複時挙動を確認できる', async ({
    page,
  }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);
    await page.goto('/operator');

    const session1Row = sessionRowByTitle(page, 'Session 1');
    await session1Row.getByRole('button', { name: '予約する' }).click();
    await expect(page.getByText('Session 1 を予約しました。')).toBeVisible();

    await page.getByRole('button', { name: 'マイページを更新' }).click();
    const qrImage = page.getByRole('img', { name: '受付用QRコード' });
    await expect(qrImage).toBeVisible();

    const qrPayload = await qrImage.getAttribute('data-qr-payload');
    expect(qrPayload).toBeTruthy();

    await page.getByLabel('QR payload').fill(qrPayload ?? '');

    await page.getByRole('button', { name: 'イベント受付をチェックイン' }).click();
    await expect(page.getByText(/のイベント受付チェックインを記録しました。/)).toBeVisible();

    await page.getByLabel('セッション受付').selectOption('session-1');
    await page.getByRole('button', { name: 'セッションをチェックイン' }).click();
    await expect(page.getByText(/の session-1 チェックインを記録しました。/)).toBeVisible();

    await page.getByRole('button', { name: 'イベント受付をチェックイン' }).click();
    await expect(page.getByText(/は既にイベント受付済みです。/)).toBeVisible();

    await page.getByRole('button', { name: 'セッションをチェックイン' }).click();
    await expect(page.getByText(/は session-1 で既にチェックイン済みです。/)).toBeVisible();

    const checkInSection = page.locator('section').filter({
      has: page.getByRole('heading', { name: '運営チェックイン' }),
    });

    await page.getByRole('button', { name: 'チェックイン履歴を更新' }).click();
    const historyRows = checkInSection.locator('table tbody tr');
    await expect(historyRows).toHaveCount(2);
    await expect(historyRows.filter({ hasText: 'イベント受付' })).toHaveCount(1);
    await expect(historyRows.filter({ hasText: 'セッション受付' })).toHaveCount(1);
    await expect(historyRows.filter({ hasText: 'session-1' })).toHaveCount(1);
  });
});
