import { expect, test } from '@playwright/test';
import { resolveUsableAdminToken } from './support/admin';
import { clearGuestSession } from './support/ui';

test.describe('US-12-2 管理導線 (/admin) と直接アクセス制御', () => {
  test('管理者トークンなしで /admin に直接アクセスすると拒否表示になり、管理画面は表示されない', async ({
    page,
  }) => {
    await clearGuestSession(page);
    await page.goto('/admin');

    await expect(page.getByRole('heading', { name: '管理アクセス確認' })).toBeVisible();
    await expect(page.getByText('管理権限がないため /admin の管理画面を表示できません。')).toBeVisible();
    await expect(page.getByRole('heading', { name: 'セッション管理（運営）' })).toHaveCount(0);

    await page.getByRole('link', { name: '参加者画面へ戻る' }).click();
    await expect(page).toHaveURL(/\/participant$/);
    await expect(page.getByRole('heading', { name: 'セッション一覧' })).toBeVisible();
  });

  test('/admin で管理者トークンを入力すると管理機能を利用できる', async ({ page, request }) => {
    const adminToken = await resolveUsableAdminToken(request);

    await clearGuestSession(page);
    await page.goto('/admin');

    await page.getByLabel('管理者トークン').fill(adminToken);
    await page.getByRole('button', { name: '管理一覧を取得' }).click();

    await expect(page.getByRole('heading', { name: 'セッション管理（運営）' })).toBeVisible();
    await expect.poll(async () => page.locator('tbody tr').count()).toBeGreaterThanOrEqual(16);
    await expect(page.getByRole('button', { name: '予約一覧CSVを出力' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'チェックインCSVを出力' })).toBeVisible();
  });
});
