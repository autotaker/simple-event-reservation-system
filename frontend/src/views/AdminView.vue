<template>
  <main class="admin-portal ui-shell ui-shell--admin">
    <AdminTopBar
      event-name="Tokyo Product Summit 2026"
      section-label="運営管理ポータル /admin"
      admin-name="Ops Admin"
      return-to="/admin/auth"
      return-label="認証画面へ戻る"
    />

    <AdminAccessDeniedPanel
      v-if="showForbiddenPanel"
      :message="'権限がありません。管理責任者に連絡してください。'"
    />

    <p v-if="errorMessage" class="admin-portal__feedback ui-status ui-status--error">
      {{ errorMessage }}
    </p>
    <p v-if="infoMessage" class="admin-portal__feedback ui-status ui-status--success">
      {{ infoMessage }}
    </p>

    <section v-if="hasAdminAccess && !showForbiddenPanel" class="admin-portal__body">
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
import { computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import AdminAccessDeniedPanel from '../components/admin/AdminAccessDeniedPanel.vue';
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
  downloadReservationCsv,
  downloadSessionCheckInCsv,
  createAdminSession,
  startEditSession,
  updateAdminSession,
} = useReservationApp();

const router = useRouter();
const hasAdminAccess = computed<boolean>(() => adminToken.value.trim().length > 0);
const showForbiddenPanel = computed<boolean>(
  () => errorMessage.value.includes('権限') && hasAdminAccess.value,
);

const updateCreateForm = (form: AdminForm): void => {
  createForm.value = form;
};

const updateEditForm = (form: AdminForm): void => {
  editForm.value = form;
};

onMounted(() => {
  if (!hasAdminAccess.value) {
    void router.replace('/admin/auth');
    return;
  }
  void loadAdminSessions();
});

watch(hasAdminAccess, (value) => {
  if (!value) {
    void router.replace('/admin/auth');
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
