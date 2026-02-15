import { expect, test } from '@playwright/test';
import { clearGuestSession, loginAsGuest } from './support/ui';

test.describe('US-03 セッション一覧と残席ステータス', () => {
  test('ゲストはセッション一覧で時刻・トラック・3段階ステータスを確認できる', async ({ page }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);

    await page.getByRole('button', { name: 'セッション一覧を取得' }).click();

    const rows = page.locator('tbody tr');
    expect(await rows.count()).toBeGreaterThanOrEqual(16);
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
