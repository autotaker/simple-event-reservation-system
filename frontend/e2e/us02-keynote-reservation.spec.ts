import { expect, test } from '@playwright/test';
import { installReservationMock } from './support/reservation-mock';
import { clearGuestSession, loginAsGuest } from './support/ui';

test.describe('US-02 キーノート予約', () => {
  test('ゲストはUIからキーノート予約でき、参加登録完了になる', async ({ page }) => {
    await installReservationMock(page);

    await clearGuestSession(page);
    await loginAsGuest(page, 'guest-test');

    await page.getByRole('button', { name: 'キーノートを予約' }).click();

    await expect(page.getByText('キーノートを予約しました。')).toBeVisible();
    await expect(page.getByText('参加登録: 完了')).toBeVisible();
    await expect(page.locator('li', { hasText: 'keynote' })).toBeVisible();
  });
});
