<template>
  <main class="portal">
    <AdminAuthTopBarMock
      event-name="Tokyo Product Summit 2026"
      section-label="運営管理ポータル /admin"
      admin-name="Ops Viewer"
    />

    <AdminForbiddenPanelMock />
  </main>
</template>

<script setup lang="ts">
import AdminAuthTopBarMock from './AdminAuthTopBarMock.vue';
import AdminForbiddenPanelMock from './AdminForbiddenPanelMock.vue';
</script>

<style scoped>
.portal {
  display: grid;
  gap: 12px;
  width: min(980px, 100%);
  padding: 14px;
  border-radius: 18px;
  border: 1px solid var(--semantic-color-state-danger);
  background: linear-gradient(
    160deg,
    var(--semantic-color-state-danger-soft),
    var(--semantic-color-bg-surface) 50%,
    var(--semantic-color-bg-subtle)
  );
  font-family: var(--font-family-sans);
}

@media (max-width: 900px) {
  .portal {
    width: min(390px, 100%);
    padding: 12px;
  }
}
</style>
