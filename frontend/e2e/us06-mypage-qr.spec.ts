import { expect, test } from '@playwright/test';
import { installReservationMock } from './support/reservation-mock';
import { clearGuestSession, loginAsGuest } from './support/ui';

test.describe('US-06 マイページで予約一覧とQRコードを表示できる', () => {
  test('ログイン中ユーザーはマイページで予約一覧とQRコードを表示できる', async ({ page }) => {
    await installReservationMock(page);

    await clearGuestSession(page);
    await loginAsGuest(page, 'guest-test');

    await page.getByRole('button', { name: 'キーノートを予約' }).click();
    const myPagePanel = page.locator('section', {
      has: page.getByRole('heading', { name: 'マイページ' }),
    });
    await expect(myPagePanel.getByRole('heading', { name: 'マイページ' })).toBeVisible();
    await expect(myPagePanel.locator('li', { hasText: 'Opening Keynote' }).first()).toBeVisible();
    await expect(myPagePanel.locator('li', { hasText: '09:00 | Keynote' }).first()).toBeVisible();
    await expect(myPagePanel.locator('li', { hasText: '不明なセッション' })).toHaveCount(0);

    const qrImage = page.getByRole('img', { name: '受付用QRコード' });
    await expect(qrImage).toBeVisible();
    await expect(qrImage).toHaveAttribute('src', /^data:image\/png;base64,/);
    await expect(qrImage).toHaveAttribute('data-qr-payload', /guest-test/);
    await expect(qrImage).not.toHaveAttribute('src', /api\.qrserver\.com/);
  });

  test('未ログイン時はマイページがアクセス制御される', async ({ page }) => {
    await installReservationMock(page);

    await clearGuestSession(page);
    await expect(page.getByText('ログイン後に受付QRコードが表示されます。')).toBeVisible();
    await expect(page.getByText('予約はありません。')).toHaveCount(0);
    await expect(page.getByRole('img', { name: '受付用QRコード' })).toHaveCount(0);
  });
});
