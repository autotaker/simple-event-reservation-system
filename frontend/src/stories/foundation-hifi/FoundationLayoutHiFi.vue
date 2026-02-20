<template>
  <main class="layout" :class="{ 'layout--mobile': isMobile }">
    <PageShellMock :compact="isMobile">
      <header class="layout__header">
        <h1>CSS Foundation Layout</h1>
        <BaseButtonMock label="Primary action" />
      </header>

      <section class="layout__controls">
        <BaseButtonMock label="Secondary action" tone="secondary" />
        <BaseButtonMock label="Disabled action" disabled />
      </section>

      <BaseFieldMock label="参加者ID" placeholder="guest-001" :state="fieldState" />
      <BaseTableMock :rows="rows" />
      <StatusMessageMock :tone="messageTone" :message="messageText" />
    </PageShellMock>
  </main>
</template>

<script setup lang="ts">
import BaseButtonMock from './BaseButtonMock.vue';
import BaseFieldMock from './BaseFieldMock.vue';
import BaseTableMock from './BaseTableMock.vue';
import PageShellMock from './PageShellMock.vue';
import StatusMessageMock from './StatusMessageMock.vue';

const props = withDefaults(
  defineProps<{
    mode?: 'default' | 'loading' | 'success' | 'error';
    isMobile?: boolean;
  }>(),
  {
    mode: 'default',
    isMobile: true,
  },
);

const rows = [
  { id: 'S-101', title: 'Design Tokens Overview', action: '編集' },
  { id: 'S-204', title: 'UX Feedback Ops', action: '確認' },
];

const messageTone =
  props.mode === 'loading'
    ? 'loading'
    : props.mode === 'success'
      ? 'success'
      : props.mode === 'error'
        ? 'error'
        : 'info';

const messageText =
  props.mode === 'loading'
    ? '更新中です。完了までお待ちください。'
    : props.mode === 'success'
      ? '保存が完了しました。'
      : props.mode === 'error'
        ? '保存に失敗しました。入力内容を確認してください。'
        : '基盤CSSの既定レイアウトを表示しています。';

const fieldState = props.mode === 'error' ? 'error' : 'default';
</script>

<style scoped>
.layout {
  padding: 16px;
  background: var(--semantic-color-bg-canvas);
}

.layout--mobile {
  width: 390px;
}

.layout__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.layout__header h1 {
  margin: 0;
  font-size: 18px;
  color: var(--semantic-color-text-primary);
}

.layout__controls {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
