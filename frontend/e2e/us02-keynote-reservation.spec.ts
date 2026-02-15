import { expect, test } from '@playwright/test';

test.describe('US-02 キーノート予約', () => {
  test('ゲストはUIからキーノート予約でき、参加登録完了になる', async ({ page }) => {
    await page.goto('/');
    await page.evaluate(() => {
      localStorage.removeItem('guestAccessToken');
      localStorage.removeItem('guestId');
    });
    await page.reload();

    await page.getByRole('button', { name: 'ゲストでログイン' }).click();
    await expect(page.getByText('ログイン中:')).toBeVisible();

    await page.getByRole('button', { name: 'キーノートを予約' }).click();

    await expect(page.getByText('キーノートを予約しました。')).toBeVisible();
    await expect(page.getByText('参加登録: 完了')).toBeVisible();
    await expect(page.locator('li', { hasText: 'keynote' })).toBeVisible();
  });
});
