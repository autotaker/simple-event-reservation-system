import { expect, test } from '@playwright/test';
import { resolveAdminCredentials } from './support/admin';
import { clearGuestSession } from './support/ui';

test.describe('US-12-2 管理導線 (/admin) と直接アクセス制御', () => {
  test('認証情報なしで /admin に直接アクセスすると /admin/auth に遷移し、管理画面は表示されない', async ({
    page,
  }) => {
    await clearGuestSession(page);
    await page.goto('/admin');

    await expect(page).toHaveURL(/\/admin\/auth$/);
    await expect(page.getByRole('heading', { name: '管理者ログイン' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'セッション管理（運営）' })).toHaveCount(0);
  });

  test('/admin/auth でログインすると管理機能を利用できる', async ({ page }) => {
    const adminCredentials = resolveAdminCredentials();

    await clearGuestSession(page);
    await page.goto('/admin/auth');

    await page.getByLabel('運用者ID（operatorId）').fill(adminCredentials.operatorId);
    await page.getByLabel('パスワード').fill(adminCredentials.password);
    await page.getByRole('button', { name: 'ログインして管理画面へ進む' }).click();
    await expect(page).toHaveURL(/\/admin$/);

    await expect(page.getByRole('heading', { name: 'セッション管理（運営）' })).toBeVisible();
    await expect.poll(async () => page.locator('tbody tr').count()).toBeGreaterThanOrEqual(16);
    await expect(page.getByRole('button', { name: '予約一覧CSVを出力' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'チェックインCSVを出力' })).toBeVisible();
  });
});
