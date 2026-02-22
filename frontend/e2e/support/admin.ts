import { existsSync, readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import type { APIRequestContext } from '@playwright/test';

const stripQuotes = (value: string): string => value.replace(/^['"]|['"]$/g, '');

const readAdminCredentialFromLocalConfig = (
  key: 'admin-operator-id' | 'admin-password',
): string | null => {
  const localConfigPath = resolve(
    process.cwd(),
    '../backend/src/main/resources/application-local.yml',
  );
  if (!existsSync(localConfigPath)) {
    return null;
  }

  const content = readFileSync(localConfigPath, 'utf8');
  const match = content.match(new RegExp(`^\\s*${key}:\\s*(.+)\\s*$`, 'm'));
  if (!match || !match[1]) {
    return null;
  }

  const parsed = stripQuotes(match[1].trim());
  return parsed.length > 0 ? parsed : null;
};

export const resolveAdminCredentials = (): { operatorId: string; password: string } => {
  const operatorId =
    process.env.E2E_ADMIN_OPERATOR_ID?.trim() ??
    readAdminCredentialFromLocalConfig('admin-operator-id') ??
    'change-me-local-admin';
  const password =
    process.env.E2E_ADMIN_PASSWORD?.trim() ??
    readAdminCredentialFromLocalConfig('admin-password') ??
    'change-me-local-admin-password';
  return { operatorId, password };
};

export const resolveUsableAdminToken = async (request: APIRequestContext): Promise<string> => {
  const { operatorId, password } = resolveAdminCredentials();
  const response = await request.post('http://127.0.0.1:8080/api/auth/admin', {
    data: {
      operatorId,
      password,
    },
  });
  if (response.status() === 200) {
    const payload = (await response.json()) as { accessToken: string };
    return payload.accessToken;
  }
  throw new Error(
    '利用可能な管理者認証情報を解決できませんでした。E2E_ADMIN_OPERATOR_ID / E2E_ADMIN_PASSWORD を設定してください。',
  );
};
