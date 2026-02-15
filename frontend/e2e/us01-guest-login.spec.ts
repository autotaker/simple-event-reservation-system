import { expect, test } from '@playwright/test';

test.describe('US-01 ゲストログイン', () => {
  test('未ログイン状態で保護APIにアクセスすると401になる', async ({ request }) => {
    const response = await request.get('http://127.0.0.1:8080/api/reservations');
    expect(response.status()).toBe(401);
  });

  test('ゲストログイン導線があり、ログイン後にアプリ利用を開始できる', async ({ page }) => {
    await page.goto('/');
    await page.evaluate(() => {
      localStorage.removeItem('guestAccessToken');
      localStorage.removeItem('guestId');
    });
    await page.reload();

    await expect(page.getByRole('button', { name: 'ゲストでログイン' })).toBeVisible();
    await expect(page.getByRole('button', { name: '予約一覧を取得' })).toBeDisabled();
    await expect(page.getByRole('button', { name: 'キーノートを予約' })).toBeDisabled();

    await page.getByRole('button', { name: 'ゲストでログイン' }).click();

    await expect(page.getByText('ログイン中:')).toBeVisible();
    await expect(page.getByRole('button', { name: '予約一覧を取得' })).toBeEnabled();
    await expect(page.getByRole('button', { name: 'キーノートを予約' })).toBeEnabled();
  });

  test('ゲストログイン状態で予約系APIが利用できる', async ({ page, request }) => {
    await page.goto('/');
    await page.evaluate(() => {
      localStorage.removeItem('guestAccessToken');
      localStorage.removeItem('guestId');
    });
    await page.reload();

    await page.getByRole('button', { name: 'ゲストでログイン' }).click();
    await expect(page.getByText('ログイン中:')).toBeVisible();

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
