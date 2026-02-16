<template>
  <main class="portal" :class="`portal--${mode}`">
    <AdminTopBarMock
      event-name="Tokyo Product Summit 2026"
      section-label="運営管理ポータル /admin"
      admin-name="Ops Admin"
    />

    <section class="portal__body">
      <AdminSessionTableMock :sessions="sessions" :disabled="mode === 'loading'" />

      <aside class="portal__side">
        <AdminSessionEditorMock :disabled="mode === 'loading'" />
        <AdminCsvExportPanelMock :disabled="mode === 'loading'" />
      </aside>
    </section>

    <AdminAccessDeniedPanelMock v-if="mode === 'error'" />

    <p v-if="mode === 'loading'" class="portal__feedback portal__feedback--loading">
      権限判定と更新処理を実行中です...
    </p>
    <p v-if="mode === 'success'" class="portal__feedback portal__feedback--success">
      管理操作が完了しました
    </p>
  </main>
</template>

<script setup lang="ts">
import AdminAccessDeniedPanelMock from './AdminAccessDeniedPanelMock.vue';
import AdminCsvExportPanelMock from './AdminCsvExportPanelMock.vue';
import AdminSessionEditorMock from './AdminSessionEditorMock.vue';
import AdminSessionTableMock from './AdminSessionTableMock.vue';
import AdminTopBarMock from './AdminTopBarMock.vue';

defineProps<{
  mode: 'default' | 'loading' | 'success' | 'error';
}>();

const sessions = [
  { id: 'a1', slot: '10:00', track: 'Track A', title: 'Keynote: Product Vision' },
  { id: 'a2', slot: '11:30', track: 'Track B', title: 'API Design Clinic' },
  { id: 'a3', slot: '14:00', track: 'Track C', title: 'Secure Check-in Operations' },
];
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
  grid-template-columns: 1.3fr 1fr;
}

.portal__side {
  display: grid;
  gap: 10px;
  align-content: start;
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
}
</style>
