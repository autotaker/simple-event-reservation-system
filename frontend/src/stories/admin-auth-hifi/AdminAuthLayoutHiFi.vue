<template>
  <main class="portal" :class="`portal--${mode}`">
    <AdminAuthTopBarMock
      event-name="Tokyo Product Summit 2026"
      section-label="管理者認証ポータル /admin/auth"
      admin-name="Ops Admin"
    />

    <section v-if="mode !== 'error'" class="portal__body">
      <AdminLoginCardMock :disabled="mode === 'loading'" />
      <AdminTokenStatusPanelMock
        :ttl-label="tokenState.ttl"
        :last-refresh-at="tokenState.lastRefresh"
        :api-status="tokenState.apiStatus"
        :disabled="mode === 'loading'"
      />
      <AdminSessionRevocationPanelMock :disabled="mode === 'loading'" />
    </section>

    <AdminAuthDeniedPanelMock v-if="mode === 'error'" />

    <p v-if="mode === 'loading'" class="portal__feedback portal__feedback--loading">
      認証を確認中です。短命トークンを発行しています...
    </p>
    <p v-if="mode === 'success'" class="portal__feedback portal__feedback--success">
      トークンを更新しました。管理APIを継続利用できます。
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
  mode: 'default' | 'loading' | 'success' | 'error';
}>();

const tokenState =
  props.mode === 'success'
    ? {
        ttl: '14:59',
        lastRefresh: '2026-02-21 09:35',
        apiStatus: '利用可能（更新済み）',
      }
    : {
        ttl: '09:45',
        lastRefresh: '2026-02-21 09:20',
        apiStatus: '利用可能',
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

.portal--loading {
  border-color: var(--semantic-color-state-warning);
}

.portal--success {
  border-color: var(--semantic-color-state-success);
}

.portal--error {
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
