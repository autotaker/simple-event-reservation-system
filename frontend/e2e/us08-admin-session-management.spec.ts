import { expect, test } from '@playwright/test';
import { resolveAdminCredentials } from './support/admin';
import { clearGuestSession, loginAsGuest, participantSessionCellByTitle } from './support/ui';

test.describe('US-08 運営がセッション情報を管理できる', () => {
  test('未認証で管理APIにアクセスすると401になる', async ({ request }) => {
    const response = await request.get('http://127.0.0.1:8080/api/admin/sessions');
    expect(response.status()).toBe(401);
  });

  test('運営はセッションを作成・編集でき、整合性制約と参加者一覧反映を確認できる', async ({
    page,
    request,
  }) => {
    const adminCredentials = resolveAdminCredentials();
    const uniqueId = Date.now();
    const createdTitle = `E2E Session ${uniqueId}`;
    const updatedTitle = `E2E Updated Session ${uniqueId}`;

    await clearGuestSession(page);
    await page.goto('/participant');
    await loginAsGuest(page);
    await page.goto('/admin/auth');
    await page.getByLabel('運用者ID（operatorId）').fill(adminCredentials.operatorId);
    await page.getByLabel('パスワード').fill(adminCredentials.password);
    await page.getByRole('button', { name: 'ログインして管理画面へ進む' }).click();
    await expect(page).toHaveURL(/\/admin$/);

    const adminSection = page.locator('section').filter({
      has: page.getByRole('heading', { name: 'セッション管理（運営）' }),
    });
    await expect(adminSection).toBeVisible();

    const createForm = adminSection.locator('form').first();
    await createForm.getByLabel('タイトル').fill(createdTitle);
    await createForm.getByLabel('開始時刻').fill('16:45');
    await createForm.getByLabel('トラック').fill('Track E2E');
    await createForm.getByLabel('定員').fill('15');
    await createForm.getByRole('button', { name: 'セッション作成' }).click();

    await expect(page.getByText('セッションを作成しました。')).toBeVisible();

    const createdAdminRow = adminSection.locator('tbody tr', { hasText: createdTitle });
    await expect(createdAdminRow).toBeVisible();
    await expect(createdAdminRow).toContainText('16:45');
    await expect(createdAdminRow).toContainText('Track E2E');
    await expect(createdAdminRow).toContainText('15');

    const sessionId = (await createdAdminRow.locator('td').first().innerText()).trim();

    await createdAdminRow.getByRole('button', { name: '編集' }).click();
    const editForm = adminSection.locator('form').nth(1);
    await expect(
      editForm.getByRole('heading', { name: new RegExp(`^編集: ${sessionId}$`) }),
    ).toBeVisible();
    await editForm.getByLabel('タイトル').fill(updatedTitle);
    await editForm.getByLabel('開始時刻').fill('17:15');
    await editForm.getByLabel('トラック').fill('Track E2E Updated');
    await editForm.getByLabel('定員').fill('8');
    await editForm.getByRole('button', { name: '更新' }).click();

    await expect(page.getByText('セッションを更新しました。')).toBeVisible();

    const updatedAdminRow = adminSection.locator('tbody tr', { hasText: updatedTitle });
    await expect(updatedAdminRow).toBeVisible();
    await expect(updatedAdminRow).toContainText('17:15');
    await expect(updatedAdminRow).toContainText('Track E2E Updated');
    await expect(updatedAdminRow).toContainText('8');

    await page.goto('/participant');
    await page.getByLabel('session list').getByRole('button', { name: '更新' }).click();
    await expect(page.getByRole('rowheader', { name: '17:15' })).toBeVisible();
    await expect(page.getByRole('columnheader', { name: 'Track E2E Updated' })).toBeVisible();
    const updatedParticipantCell = participantSessionCellByTitle(page, updatedTitle);
    await expect(updatedParticipantCell).toBeVisible();
    await expect(updatedParticipantCell.getByText('残りわずか')).toBeVisible();

    await page.goto('/admin');

    const guest1 = await request.post('http://127.0.0.1:8080/api/auth/guest');
    expect(guest1.status()).toBe(200);
    const guest1Token = (await guest1.json()).accessToken as string;

    const guest2 = await request.post('http://127.0.0.1:8080/api/auth/guest');
    expect(guest2.status()).toBe(200);
    const guest2Token = (await guest2.json()).accessToken as string;

    const reserve1 = await request.post(
      `http://127.0.0.1:8080/api/reservations/sessions/${encodeURIComponent(sessionId)}`,
      {
        headers: {
          Authorization: `Bearer ${guest1Token}`,
        },
      },
    );
    expect(reserve1.status()).toBe(200);

    const reserve2 = await request.post(
      `http://127.0.0.1:8080/api/reservations/sessions/${encodeURIComponent(sessionId)}`,
      {
        headers: {
          Authorization: `Bearer ${guest2Token}`,
        },
      },
    );
    expect(reserve2.status()).toBe(200);

    await updatedAdminRow.getByRole('button', { name: '編集' }).click();
    const editFormAfterReservation = adminSection.locator('form').nth(1);
    await expect(
      editFormAfterReservation.getByRole('heading', { name: new RegExp(`^編集: ${sessionId}$`) }),
    ).toBeVisible();
    await editFormAfterReservation.getByLabel('定員').fill('1');
    await editFormAfterReservation.getByRole('button', { name: '更新' }).click();

    await expect(page.getByText('現在の予約数を下回る定員には変更できません。')).toBeVisible();
    await expect(adminSection.locator('tbody tr', { hasText: updatedTitle })).toContainText('8');
  });
});
