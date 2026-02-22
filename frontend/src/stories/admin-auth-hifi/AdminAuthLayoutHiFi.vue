<template>
  <main class="portal" :class="`portal--${mode}`">
    <AdminAuthTopBarMock
      event-name="Tokyo Product Summit 2026"
      section-label="管理者認証ポータル /admin/auth"
      admin-name="Ops Admin"
    />

    <section
      v-if="mode === 'unauthenticated' || mode === 'authenticating'"
      class="portal__body portal__body--login-only"
    >
      <AdminLoginCardMock :disabled="mode === 'authenticating'" />
    </section>

    <section v-if="mode === 'authenticated'" class="portal__body">
      <AdminLoginCardMock :disabled="true" />
      <AdminTokenStatusPanelMock
        :operator-id="tokenState.operatorId"
        :expires-at="tokenState.expiresAt"
        :api-status="tokenState.apiStatus"
        :disabled="false"
      />
      <AdminSessionRevocationPanelMock :disabled="false" />
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

    <AdminAuthDeniedPanelMock
      v-if="mode === 'invalid'"
      heading="認証情報を確認できません"
      message="未認証、または不正なトークンです。sessionStorageのトークンを破棄し、再ログインしてください。"
    />

    <p v-if="mode === 'authenticating'" class="portal__feedback portal__feedback--loading">
      認証中です。`operatorId + password` を検証しています...
    </p>
    <p v-if="mode === 'authenticated'" class="portal__feedback portal__feedback--success">
      認証済みです。/admin へ遷移して管理操作を開始できます。
    </p>
    <button v-if="mode === 'authenticated'" type="button" class="portal__cta">
      管理画面へ進む
    </button>
  </main>
</template>

<script setup lang="ts">
import AdminAuthDeniedPanelMock from './AdminAuthDeniedPanelMock.vue';
import AdminAuthTopBarMock from './AdminAuthTopBarMock.vue';
import AdminLoginCardMock from './AdminLoginCardMock.vue';
import AdminSessionRevocationPanelMock from './AdminSessionRevocationPanelMock.vue';
import AdminTokenStatusPanelMock from './AdminTokenStatusPanelMock.vue';

const props = defineProps<{
  mode: 'unauthenticated' | 'authenticating' | 'authenticated' | 'expired' | 'revoked' | 'invalid';
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

.portal__body--login-only {
  grid-template-columns: 1fr;
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
.portal--revoked,
.portal--invalid {
  border-color: var(--semantic-color-state-danger);
}

.portal__cta {
  width: fit-content;
  min-width: 140px;
  height: 34px;
  border: none;
  border-radius: 10px;
  padding: 0 12px;
  background: var(--semantic-color-state-success);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
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
