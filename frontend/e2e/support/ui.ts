import { expect, type Page } from '@playwright/test';

export const clearGuestSession = async (page: Page): Promise<void> => {
  await page.goto('/participant');
  await page.evaluate(() => {
    localStorage.removeItem('guestAccessToken');
    localStorage.removeItem('guestId');
    localStorage.removeItem('adminAccessToken');
  });
  await page.reload();
};

export const loginAsGuest = async (page: Page, expectedGuestId?: string): Promise<void> => {
  await page.getByRole('button', { name: 'ゲストでログイン' }).click();
  await expect.poll(async () => page.evaluate(() => localStorage.getItem('guestId'))).toBeTruthy();
  if (expectedGuestId) {
    await expect
      .poll(async () => page.evaluate(() => localStorage.getItem('guestId')))
      .toBe(expectedGuestId);
  }
};

export const sessionRowByTitle = (page: Page, title: string) =>
  page
    .locator('tbody tr')
    .filter({ has: page.locator('td', { hasText: new RegExp(`^${title}$`) }) });

export const participantSessionCardByTitle = (page: Page, title: string) =>
  page
    .locator('article')
    .filter({ has: page.getByRole('heading', { name: new RegExp(`^${title}$`) }) });
