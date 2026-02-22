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

  test('再ログイン導線で機密入力とエラーメッセージが初期化される', async ({ page }) => {
    const adminCredentials = resolveAdminCredentials();

    await clearGuestSession(page);
    await page.goto('/admin/auth');

    await page.getByLabel('運用者ID（operatorId）').fill(adminCredentials.operatorId);
    const passwordInput = page.getByLabel('パスワード');
    await passwordInput.fill('wrong-password');
    await expect(passwordInput).toHaveValue('wrong-password');
    await page.getByRole('button', { name: 'ログインして管理画面へ進む' }).click();

    await expect(page.getByRole('heading', { name: '認証情報を確認できません' })).toBeVisible();
    const authError = page.locator('.ui-status--error');
    await expect(authError).toBeVisible();

    await page.getByRole('button', { name: '再ログインする' }).click();
    await expect(page.getByRole('heading', { name: '管理者ログイン' })).toBeVisible();
    await expect(passwordInput).toHaveValue('');
    await expect(authError).toHaveCount(0);
  });

  test('認証画面へ戻っても認証状態に operatorId と有効期限が表示される', async ({ page }) => {
    const adminCredentials = resolveAdminCredentials();

    await clearGuestSession(page);
    await page.goto('/admin/auth');

    await page.getByLabel('運用者ID（operatorId）').fill(adminCredentials.operatorId);
    await page.getByLabel('パスワード').fill(adminCredentials.password);
    await page.getByRole('button', { name: 'ログインして管理画面へ進む' }).click();
    await expect(page).toHaveURL(/\/admin$/);

    await page.getByRole('link', { name: '認証画面へ戻る' }).click();
    await expect(page).toHaveURL(/\/admin\/auth$/);
    await expect(page.getByRole('heading', { name: '認証状態' })).toBeVisible();
    await expect(page.getByText(`operatorId: ${adminCredentials.operatorId}`)).toBeVisible();
    await expect(page.getByText(/有効期限（固定30分）:\s*\S+/)).toBeVisible();
  });

  test('390x844 では管理一覧をカード表示し、導線ボタンが表示される', async ({ page }) => {
    const adminCredentials = resolveAdminCredentials();

    await page.setViewportSize({ width: 390, height: 844 });
    await clearGuestSession(page);
    await page.goto('/admin/auth');

    await expect(page.getByRole('link', { name: '参加者画面へ戻る' })).toBeVisible();
    await page.getByLabel('運用者ID（operatorId）').fill(adminCredentials.operatorId);
    await page.getByLabel('パスワード').fill(adminCredentials.password);
    await page.getByRole('button', { name: 'ログインして管理画面へ進む' }).click();
    await expect(page).toHaveURL(/\/admin$/);

    await expect(page.getByRole('link', { name: '認証画面へ戻る' })).toBeVisible();
    await expect(page.locator('.table-wrap__mobile-item').first()).toBeVisible();
  });
});
