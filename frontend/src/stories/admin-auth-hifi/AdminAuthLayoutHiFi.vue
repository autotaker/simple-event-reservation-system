<template>
  <main class="portal" :class="`portal--${mode}`">
    <AdminAuthTopBarMock
      event-name="Tokyo Product Summit 2026"
      section-label="管理者認証ポータル /admin/auth"
      admin-name="Ops Admin"
    />

    <section v-if="mode !== 'expired' && mode !== 'revoked'" class="portal__body">
      <AdminLoginCardMock :disabled="mode === 'authenticating'" />
      <AdminTokenStatusPanelMock
        :operator-id="tokenState.operatorId"
        :expires-at="tokenState.expiresAt"
        :api-status="tokenState.apiStatus"
        :disabled="mode === 'authenticating'"
      />
      <AdminSessionRevocationPanelMock :disabled="mode === 'authenticating'" />
    </section>

    <AdminAuthDeniedPanelMock
      v-if="mode === 'expired'"
      heading="トークンの有効期限が切れました"
      message="管理APIが401を返しました。sessionStorageのトークンを破棄し、再ログインしてください。"
    />

    <AdminAuthDeniedPanelMock
      v-if="mode === 'revoked'"
      heading="トークンは失効済みです"
      message="ログアウト済み、または失効されたトークンです。再ログインして新しいトークンを発行してください。"
    />

    <p v-if="mode === 'authenticating'" class="portal__feedback portal__feedback--loading">
      認証中です。`operatorId + password` を検証しています...
    </p>
    <p v-if="mode === 'authenticated'" class="portal__feedback portal__feedback--success">
      認証済みです。/admin へ遷移して管理操作を開始できます。
    </p>
  </main>
</template>

<script setup lang="ts">
import AdminAuthDeniedPanelMock from './AdminAuthDeniedPanelMock.vue';
import AdminAuthTopBarMock from './AdminAuthTopBarMock.vue';
import AdminLoginCardMock from './AdminLoginCardMock.vue';
import AdminSessionRevocationPanelMock from './AdminSessionRevocationPanelMock.vue';
import AdminTokenStatusPanelMock from './AdminTokenStatusPanelMock.vue';

const props = defineProps<{
  mode: 'unauthenticated' | 'authenticating' | 'authenticated' | 'expired' | 'revoked';
}>();

const tokenState =
  props.mode === 'authenticated'
    ? {
        operatorId: 'ops_admin_01',
        expiresAt: '2026-02-22T10:00:00+09:00',
        apiStatus: '利用可能',
      }
    : {
        operatorId: 'ops_admin_01',
        expiresAt: '2026-02-22T09:30:00+09:00',
        apiStatus: '認証前',
      };
</script>

<style scoped>
.portal {
  display: grid;
  gap: 12px;
  width: min(980px, 100%);
  padding: 14px;
  border-radius: 18px;
  border: 1px solid var(--semantic-color-state-warning);
  background: linear-gradient(
    160deg,
    var(--semantic-color-state-warning-soft),
    var(--semantic-color-bg-surface) 48%,
    var(--semantic-color-bg-subtle)
  );
  font-family: var(--font-family-sans);
}

.portal__body {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.portal__body :deep(section:last-child) {
  grid-column: span 2;
}

.portal__feedback {
  margin: 0;
  padding: 10px 12px;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 700;
}

.portal__feedback--loading {
  background: var(--semantic-color-state-warning-soft);
  color: var(--semantic-color-state-warning);
}

.portal__feedback--success {
  background: var(--semantic-color-state-success-soft);
  color: var(--semantic-color-state-success);
}

.portal--authenticating {
  border-color: var(--semantic-color-state-warning);
}

.portal--authenticated {
  border-color: var(--semantic-color-state-success);
}

.portal--expired,
.portal--revoked {
  border-color: var(--semantic-color-state-danger);
}

@media (max-width: 900px) {
  .portal {
    width: min(390px, 100%);
    padding: 12px;
  }

  .portal__body {
    grid-template-columns: 1fr;
  }

  .portal__body :deep(section:last-child) {
    grid-column: auto;
  }
}
</style>
