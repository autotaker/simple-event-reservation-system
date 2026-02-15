import { expect, test } from '@playwright/test';

test.describe('US-03 セッション一覧と残席ステータス', () => {
  test('ゲストはセッション一覧で時刻・トラック・3段階ステータスを確認できる', async ({ page }) => {
    await page.goto('/');
    await page.evaluate(() => {
      localStorage.removeItem('guestAccessToken');
      localStorage.removeItem('guestId');
    });
    await page.reload();

    await page.getByRole('button', { name: 'ゲストでログイン' }).click();
    await expect(page.getByText('ログイン中:')).toBeVisible();

    await page.getByRole('button', { name: 'セッション一覧を取得' }).click();

    const rows = page.locator('tbody tr');
    await expect(rows).toHaveCount(16);
    await expect(page.getByRole('columnheader', { name: '開始時刻' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'トラック' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: '残席ステータス' })).toBeVisible();

    const statusCells = page.locator('tbody tr td:nth-child(4)');
    const statuses = await statusCells.allInnerTexts();
    for (const status of statuses) {
      expect(['受付中', '残りわずか', '満席']).toContain(status.trim());
    }
    await expect(statusCells.filter({ hasText: /\d/ })).toHaveCount(0);
  });
});
