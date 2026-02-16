<template>
  <main class="admin-portal">
    <header class="admin-portal__header">
      <h1>運営導線 /admin</h1>
      <RouterLink to="/participant">参加者画面へ戻る</RouterLink>
    </header>

    <section class="admin-portal__gate">
      <h2>管理アクセス確認</h2>
      <label>
        管理者トークン
        <input v-model="adminToken" type="password" placeholder="admin token" />
      </label>
      <p v-if="!hasAdminAccess" class="admin-portal__feedback admin-portal__feedback--forbidden">
        管理権限がないため /admin
        の管理画面を表示できません。管理者トークンを設定するか、参加者画面へ戻ってください。
      </p>
    </section>

    <p v-if="errorMessage" class="admin-portal__feedback admin-portal__feedback--error">
      {{ errorMessage }}
    </p>
    <p v-if="infoMessage" class="admin-portal__feedback admin-portal__feedback--success">
      {{ infoMessage }}
    </p>

    <section v-if="hasAdminAccess">
      <h2>セッション管理（運営）</h2>
      <button type="button" :disabled="!adminToken" @click="loadAdminSessions">
        管理一覧を取得
      </button>
      <button type="button" :disabled="!adminToken" @click="downloadReservationCsv">
        予約一覧CSVを出力
      </button>
      <button type="button" :disabled="!adminToken" @click="downloadSessionCheckInCsv">
        チェックインCSVを出力
      </button>

      <form @submit.prevent="createAdminSession">
        <h3>新規作成</h3>
        <label>
          タイトル
          <input v-model="createForm.title" type="text" required />
        </label>
        <label>
          開始時刻
          <input v-model="createForm.startTime" type="time" required />
        </label>
        <label>
          トラック
          <input v-model="createForm.track" type="text" required />
        </label>
        <label>
          定員
          <input v-model="createForm.capacity" type="number" min="1" required />
        </label>
        <button type="submit" :disabled="!adminToken">セッション作成</button>
      </form>

      <table v-if="adminSessions.length > 0">
        <thead>
          <tr>
            <th scope="col">ID</th>
            <th scope="col">開始時刻</th>
            <th scope="col">トラック</th>
            <th scope="col">タイトル</th>
            <th scope="col">定員</th>
            <th scope="col">予約数</th>
            <th scope="col">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="session in adminSessions" :key="session.sessionId">
            <td>{{ session.sessionId }}</td>
            <td>{{ session.startTime }}</td>
            <td>{{ session.track }}</td>
            <td>{{ session.title }}</td>
            <td>{{ session.capacity }}</td>
            <td>{{ session.reservedCount }}</td>
            <td>
              <button type="button" :disabled="!adminToken" @click="startEditSession(session)">
                編集
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <form v-if="editForm.sessionId" @submit.prevent="updateAdminSession">
        <h3>編集: {{ editForm.sessionId }}</h3>
        <label>
          タイトル
          <input v-model="editForm.title" type="text" required />
        </label>
        <label>
          開始時刻
          <input v-model="editForm.startTime" type="time" required />
        </label>
        <label>
          トラック
          <input v-model="editForm.track" type="text" required />
        </label>
        <label>
          定員
          <input v-model="editForm.capacity" type="number" min="1" required />
        </label>
        <button type="submit" :disabled="!adminToken">更新</button>
        <button type="button" @click="clearEditForm">キャンセル</button>
      </form>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { RouterLink } from 'vue-router';
import { useReservationApp } from '../composables/useReservationApp';

const {
  adminToken,
  adminSessions,
  errorMessage,
  infoMessage,
  createForm,
  editForm,
  clearEditForm,
  loadAdminSessions,
  downloadReservationCsv,
  downloadSessionCheckInCsv,
  createAdminSession,
  startEditSession,
  updateAdminSession,
} = useReservationApp();

const hasAdminAccess = computed<boolean>(() => adminToken.value.trim().length > 0);

onMounted(() => {
  if (hasAdminAccess.value) {
    void loadAdminSessions();
  }
});
</script>

<style scoped>
.admin-portal {
  display: grid;
  gap: 16px;
  width: min(960px, 100%);
  margin: 0 auto;
  padding: 20px;
}

.admin-portal__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-portal__header h1 {
  margin: 0;
}

.admin-portal__gate {
  display: grid;
  gap: 8px;
}

.admin-portal__gate h2 {
  margin: 0;
}

.admin-portal__feedback {
  margin: 0;
}

.admin-portal__feedback--forbidden {
  color: #8a5a00;
}

.admin-portal__feedback--error {
  color: #b20000;
}

.admin-portal__feedback--success {
  color: #136428;
}
</style>
