<template>
  <main class="admin-portal ui-shell ui-shell--admin">
    <AdminTopBar
      event-name="Tokyo Product Summit 2026"
      section-label="運営管理ポータル /admin"
      admin-name="Ops Admin"
      return-to="/participant"
      return-label="参加者画面へ戻る"
    />

    <AdminAccessGate v-model="adminToken" />
    <button
      v-if="hasAdminAccess"
      class="ui-button ui-button--secondary"
      type="button"
      @click="endAdminSession"
    >
      管理者セッションを終了
    </button>
    <AdminAccessDeniedPanel
      v-if="!hasAdminAccess"
      :message="'管理権限がないため /admin の管理画面を表示できません。管理者トークンを設定するか、参加者画面へ戻ってください。'"
    />

    <p v-if="errorMessage" class="admin-portal__feedback ui-status ui-status--error">
      {{ errorMessage }}
    </p>
    <p v-if="infoMessage" class="admin-portal__feedback ui-status ui-status--success">
      {{ infoMessage }}
    </p>

    <section v-if="hasAdminAccess" class="admin-portal__body">
      <h2>セッション管理（運営）</h2>
      <div class="admin-portal__layout ui-layout-split">
        <AdminSessionTable
          :sessions="adminSessions"
          :disabled="!adminToken"
          @load="loadAdminSessions"
          @edit="startEditSession"
        />
        <aside class="admin-portal__side">
          <AdminSessionEditor
            heading="新規作成"
            :form="createForm"
            submit-label="セッション作成"
            :can-submit="Boolean(adminToken)"
            @update:form="updateCreateForm"
            @submit="createAdminSession"
          />
          <AdminSessionEditor
            v-if="editForm.sessionId"
            :heading="`編集: ${editForm.sessionId}`"
            :form="editForm"
            submit-label="更新"
            :can-submit="Boolean(adminToken)"
            show-cancel
            @update:form="updateEditForm"
            @submit="updateAdminSession"
            @cancel="clearEditForm"
          />
          <AdminCsvExportPanel
            :disabled="!adminToken"
            @download-reservations="downloadReservationCsv"
            @download-check-ins="downloadSessionCheckInCsv"
          />
        </aside>
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import AdminAccessDeniedPanel from '../components/admin/AdminAccessDeniedPanel.vue';
import AdminAccessGate from '../components/admin/AdminAccessGate.vue';
import AdminCsvExportPanel from '../components/admin/AdminCsvExportPanel.vue';
import AdminSessionEditor from '../components/admin/AdminSessionEditor.vue';
import AdminSessionTable from '../components/admin/AdminSessionTable.vue';
import AdminTopBar from '../components/admin/AdminTopBar.vue';
import { type AdminForm, useReservationApp } from '../composables/useReservationApp';

const {
  adminToken,
  adminSessions,
  errorMessage,
  infoMessage,
  createForm,
  editForm,
  clearEditForm,
  loadAdminSessions,
  endAdminSession,
  downloadReservationCsv,
  downloadSessionCheckInCsv,
  createAdminSession,
  startEditSession,
  updateAdminSession,
} = useReservationApp();

const hasAdminAccess = computed<boolean>(() => adminToken.value.trim().length > 0);

const updateCreateForm = (form: AdminForm): void => {
  createForm.value = form;
};

const updateEditForm = (form: AdminForm): void => {
  editForm.value = form;
};

onMounted(() => {
  if (hasAdminAccess.value) {
    void loadAdminSessions();
  }
});
</script>

<style scoped>
.admin-portal {
  gap: 16px;
  padding: 20px;
}

.admin-portal__feedback {
  margin: 0;
}

.admin-portal__body {
  display: grid;
  gap: 10px;
}

.admin-portal__body h2 {
  margin: 0;
}

.admin-portal__layout {
  gap: 12px;
}

.admin-portal__side {
  display: grid;
  gap: 10px;
  align-content: start;
}

@media (max-width: 900px) {
  .admin-portal {
    padding: 12px;
  }
}
</style>
