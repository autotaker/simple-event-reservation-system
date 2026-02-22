<template>
  <main class="auth-portal ui-shell ui-shell--admin">
    <AdminTopBar
      event-name="Tokyo Product Summit 2026"
      section-label="管理者認証ポータル /admin/auth"
      :admin-name="adminBadge"
      return-to="/participant"
      return-label="参加者画面へ戻る"
    />

    <section
      v-if="adminAuthState === 'unauthenticated' || adminAuthState === 'authenticating'"
      class="ui-panel auth-portal__card"
    >
      <h2>管理者ログイン</h2>
      <label class="ui-field">
        <span class="ui-field__label">運用者ID（operatorId）</span>
        <input
          v-model="adminOperatorIdInput"
          class="ui-field__input"
          type="text"
          autocomplete="username"
          :disabled="adminAuthState === 'authenticating'"
        />
      </label>
      <label class="ui-field">
        <span class="ui-field__label">パスワード</span>
        <input
          v-model="adminPasswordInput"
          class="ui-field__input"
          type="password"
          autocomplete="current-password"
          :disabled="adminAuthState === 'authenticating'"
          @keyup.enter="onLogin"
        />
      </label>
      <button
        class="ui-button ui-button--warning"
        type="button"
        :disabled="adminAuthState === 'authenticating'"
        @click="onLogin"
      >
        ログインして管理画面へ進む
      </button>
    </section>

    <section v-if="adminAuthState === 'authenticated'" class="ui-panel auth-portal__status">
      <h2>認証状態</h2>
      <ul>
        <li>operatorId: {{ adminAuthenticatedOperatorId }}</li>
        <li>有効期限（固定30分）: {{ adminTokenExpiresAt }}</li>
        <li>保存先: sessionStorage</li>
      </ul>
      <div class="auth-portal__actions">
        <button class="ui-button ui-button--primary" type="button" @click="goToAdmin">
          管理画面へ進む
        </button>
        <button class="ui-button ui-button--danger" type="button" @click="onLogout">
          ログアウトして失効する
        </button>
      </div>
    </section>

    <section v-if="adminAuthState === 'expired'" class="ui-panel auth-portal__error">
      <p class="auth-portal__error-code">401 Unauthorized</p>
      <h2>トークンの有効期限が切れました</h2>
      <p>sessionStorageのトークンを破棄しました。再ログインしてください。</p>
      <button class="ui-button ui-button--danger" type="button" @click="resetToLogin">
        再ログインする
      </button>
    </section>

    <section v-if="adminAuthState === 'revoked'" class="ui-panel auth-portal__error">
      <p class="auth-portal__error-code">401 Unauthorized</p>
      <h2>トークンは失効済みです</h2>
      <p>ログアウト済み、または失効されたトークンです。再ログインしてください。</p>
      <button class="ui-button ui-button--danger" type="button" @click="resetToLogin">
        再ログインする
      </button>
    </section>

    <section v-if="adminAuthState === 'invalid'" class="ui-panel auth-portal__error">
      <p class="auth-portal__error-code">401 Unauthorized</p>
      <h2>認証情報を確認できません</h2>
      <p>未認証、または不正なトークンです。再ログインしてください。</p>
      <button class="ui-button ui-button--danger" type="button" @click="resetToLogin">
        再ログインする
      </button>
    </section>

    <p v-if="errorMessage" class="ui-status ui-status--error">{{ errorMessage }}</p>
    <p v-if="infoMessage" class="ui-status ui-status--success">{{ infoMessage }}</p>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import AdminTopBar from '../components/admin/AdminTopBar.vue';
import { useReservationApp } from '../composables/useReservationApp';

const router = useRouter();

const {
  adminToken,
  adminOperatorIdInput,
  adminPasswordInput,
  adminAuthState,
  adminAuthenticatedOperatorId,
  adminTokenExpiresAt,
  errorMessage,
  infoMessage,
  loginAsAdmin,
  logoutAdmin,
  resetAdminLoginPrompt,
} = useReservationApp();

const adminBadge = computed<string>(() => adminAuthenticatedOperatorId.value || 'Ops Admin');

const onLogin = async (): Promise<void> => {
  const loggedIn = await loginAsAdmin();
  if (loggedIn) {
    await router.push('/admin');
  }
};

const onLogout = async (): Promise<void> => {
  await logoutAdmin();
};

const goToAdmin = async (): Promise<void> => {
  if (!adminToken.value) {
    return;
  }
  await router.push('/admin');
};

const resetToLogin = (): void => {
  resetAdminLoginPrompt();
};
</script>

<style scoped>
.auth-portal {
  display: grid;
  gap: 12px;
  padding: 20px;
}

.auth-portal__card,
.auth-portal__status,
.auth-portal__error {
  display: grid;
  gap: 10px;
}

.auth-portal__status ul {
  margin: 0;
  padding-left: 18px;
}

.auth-portal__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.auth-portal__error {
  border-color: var(--semantic-color-state-danger);
  background: var(--semantic-color-state-danger-soft);
}

.auth-portal__error-code {
  margin: 0;
  color: var(--semantic-color-state-danger);
  font-weight: 700;
  font-size: 12px;
}

@media (max-width: 900px) {
  .auth-portal {
    padding: 12px;
  }
}
</style>
