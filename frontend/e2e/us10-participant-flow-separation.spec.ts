import { expect, test } from '@playwright/test';

test.describe('US-10 参加者導線の分離', () => {
  test('/participant で参加者フローが動作し、管理機能は表示されない', async ({ page }) => {
    await page.goto('/participant');
    await page.evaluate(() => {
      localStorage.removeItem('guestAccessToken');
      localStorage.removeItem('guestId');
    });
    await page.reload();

    await expect(page.getByRole('button', { name: 'ゲストでログイン' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'セッション管理（運営）' })).toHaveCount(0);
    await expect(page.getByRole('heading', { name: '運営チェックイン' })).toHaveCount(0);

    await page.getByRole('button', { name: 'ゲストでログイン' }).click();
    await expect(page.getByRole('button', { name: 'キーノートを予約' })).toBeVisible();

    await page.getByRole('button', { name: 'キーノートを予約' }).click();
    await expect(page.getByText('参加登録: 完了')).toBeVisible();
    const myPagePanel = page.locator('section.qr-panel');
    await expect(myPagePanel.getByRole('listitem').filter({ hasText: 'Opening Keynote' })).toBeVisible();
    await expect(page.getByRole('img', { name: '受付用QRコード' })).toBeVisible();

    await page.getByRole('button', { name: 'キャンセル' }).first().click();
    await expect(myPagePanel.getByRole('listitem').filter({ hasText: 'Opening Keynote' })).toHaveCount(0);
  });
});
