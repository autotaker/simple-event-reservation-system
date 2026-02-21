import { expect, test } from '@playwright/test';
import { clearGuestSession, loginAsGuest } from './support/ui';

test.describe('US-03 セッション一覧と残席ステータス', () => {
  test('ゲストはセッション一覧で時刻・トラック・3段階ステータスを確認できる', async ({ page }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);

    const timetable = page.getByRole('table');
    await expect(timetable).toBeVisible();
    await expect(timetable.getByRole('columnheader', { name: 'Keynote' })).toBeVisible();
    await expect(timetable.getByRole('columnheader', { name: 'Track A' })).toBeVisible();

    const sessionCells = page.locator('.participant-timetable__cell').filter({
      hasNotText: 'セッションなし',
    });
    await expect.poll(async () => sessionCells.count()).toBeGreaterThanOrEqual(16);
    const cellCount = await sessionCells.count();
    for (let index = 0; index < cellCount; index += 1) {
      const cellText = await sessionCells.nth(index).innerText();
      expect(cellText).toMatch(/(受付中|残りわずか|満席|予約済み)/);
    }

    await expect(page.getByRole('rowheader', { name: '09:00' })).toBeVisible();
    await expect(page.getByRole('rowheader', { name: '10:30' })).toBeVisible();
    await expect(page.getByText('Opening Keynote')).toBeVisible();
    await expect(page.getByText('Session 1')).toBeVisible();
    await expect(page.getByText('受付中').first()).toBeVisible();
  });
});
