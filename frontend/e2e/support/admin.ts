import { existsSync, readFileSync } from 'node:fs';
import { resolve } from 'node:path';
import type { APIRequestContext } from '@playwright/test';

const stripQuotes = (value: string): string => value.replace(/^['"]|['"]$/g, '');

const readAdminTokenFromLocalConfig = (): string | null => {
  const localConfigPath = resolve(process.cwd(), '../backend/src/main/resources/application-local.yml');
  if (!existsSync(localConfigPath)) {
    return null;
  }

  const content = readFileSync(localConfigPath, 'utf8');
  const match = content.match(/^\s*admin-token:\s*(.+)\s*$/m);
  if (!match || !match[1]) {
    return null;
  }

  const parsed = stripQuotes(match[1].trim());
  return parsed.length > 0 ? parsed : null;
};

export const resolveAdminToken = (): string => {
  const fromEnv = process.env.E2E_ADMIN_TOKEN?.trim();
  if (fromEnv) {
    return fromEnv;
  }

  return readAdminTokenFromLocalConfig() ?? 'e2e-admin-token';
};

const getAdminTokenCandidates = (): string[] => {
  const candidates = [
    process.env.E2E_ADMIN_TOKEN?.trim() ?? '',
    readAdminTokenFromLocalConfig() ?? '',
    'e2e-admin-token',
    'change-me-local-admin-token',
  ].filter((token) => token.length > 0);

  return [...new Set(candidates)];
};

export const resolveUsableAdminToken = async (request: APIRequestContext): Promise<string> => {
  const candidates = getAdminTokenCandidates();
  for (const token of candidates) {
    const response = await request.get('http://127.0.0.1:8080/api/admin/sessions', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.status() !== 401) {
      return token;
    }
  }

  throw new Error('利用可能な管理者トークンを解決できませんでした。E2E_ADMIN_TOKEN を設定してください。');
};
