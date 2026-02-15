import { expect, type Page } from '@playwright/test';

export const clearGuestSession = async (page: Page): Promise<void> => {
  await page.goto('/');
  await page.evaluate(() => {
    localStorage.removeItem('guestAccessToken');
    localStorage.removeItem('guestId');
  });
  await page.reload();
};

export const loginAsGuest = async (page: Page, expectedGuestId?: string): Promise<void> => {
  await page.getByRole('button', { name: 'ゲストでログイン' }).click();

  if (expectedGuestId) {
    await expect(page.getByText(`ログイン中: ${expectedGuestId}`)).toBeVisible();
    return;
  }

  await expect(page.getByText('ログイン中:')).toBeVisible();
};

export const sessionRowByTitle = (page: Page, title: string) =>
  page
    .locator('tbody tr')
    .filter({ has: page.locator('td', { hasText: new RegExp(`^${title}$`) }) });
