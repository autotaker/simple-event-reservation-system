import { expect, test } from '@playwright/test';
import { clearGuestSession, loginAsGuest } from './support/ui';

test.describe('US-01 ゲストログイン', () => {
  test('未ログイン状態で保護APIにアクセスすると401になる', async ({ request }) => {
    const response = await request.get('http://127.0.0.1:8080/api/reservations');
    expect(response.status()).toBe(401);
  });

  test('ゲストログイン導線があり、ログイン後にアプリ利用を開始できる', async ({ page }) => {
    await clearGuestSession(page);

    await expect(page.getByRole('button', { name: 'ゲストでログイン' })).toBeVisible();
    await expect(page.getByRole('button', { name: '更新' }).nth(0)).toBeDisabled();
    await expect(page.getByRole('button', { name: '更新' }).nth(1)).toBeDisabled();
    await expect(page.getByRole('button', { name: '更新' }).nth(2)).toBeDisabled();
    await expect(page.getByRole('button', { name: 'キーノートを予約' })).toBeDisabled();
    await expect(page.getByText('ログイン後に受付QRコードが表示されます。')).toBeVisible();

    await loginAsGuest(page);
    await expect(page.getByRole('button', { name: 'キーノートを予約' })).toBeEnabled();
    await expect(page.getByRole('heading', { name: 'セッション一覧' })).toBeVisible();
    await expect(page.getByText('Opening Keynote')).toBeVisible();
    const sessionCards = page.getByLabel('session list').locator('article');
    expect(await sessionCards.count()).toBeGreaterThanOrEqual(16);
  });

  test('ゲストログイン状態で予約系APIが利用できる', async ({ page, request }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);

    const token = await page.evaluate(() => localStorage.getItem('guestAccessToken'));
    expect(token).toBeTruthy();

    const response = await request.get('http://127.0.0.1:8080/api/reservations', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    expect(response.status()).toBe(200);
    const payload = await response.json();
    expect(Array.isArray(payload.reservations)).toBe(true);
  });
});
