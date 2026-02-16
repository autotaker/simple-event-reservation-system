import { expect, test } from '@playwright/test';
import { clearGuestSession, loginAsGuest } from './support/ui';

test.describe('US-03 セッション一覧と残席ステータス', () => {
  test('ゲストはセッション一覧で時刻・トラック・3段階ステータスを確認できる', async ({ page }) => {
    await clearGuestSession(page);
    await loginAsGuest(page);

    const cards = page.locator('article');
    await expect.poll(async () => cards.count()).toBeGreaterThanOrEqual(16);
    await expect(cards.filter({ hasText: '09:00 | Keynote' })).toHaveCount(1);
    await expect(cards.filter({ hasText: '10:30 | Track A' })).toHaveCount(1);

    const cardCount = await cards.count();
    for (let index = 0; index < cardCount; index += 1) {
      const cardText = await cards.nth(index).innerText();
      expect(cardText).toMatch(/(受付中|残りわずか|満席)/);
    }
    await expect(page.getByText('受付中').first()).toBeVisible();
  });
});
