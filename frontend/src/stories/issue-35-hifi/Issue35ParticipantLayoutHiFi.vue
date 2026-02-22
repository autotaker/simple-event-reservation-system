<template>
  <main class="layout" :class="`layout--${mode}`">
    <Issue35TopBarMock />

    <section class="layout__body">
      <section class="layout__left">
        <Issue35SessionCardMock />
        <Issue35SessionCardMock />
      </section>

      <aside class="layout__right">
        <Issue35ReservationPanelMock />
        <Issue35MyPageReadablePanelMock :mode="mode" :variant="contentVariant" />
      </aside>
    </section>

    <p v-if="mode === 'loading'" class="feedback feedback--loading">予約情報を更新中です...</p>
    <p v-if="mode === 'success'" class="feedback feedback--success">予約情報を更新しました。</p>
    <p v-if="mode === 'error'" class="feedback feedback--error">予約情報の取得に失敗しました。</p>
  </main>
</template>

<script setup lang="ts">
import Issue35MyPageReadablePanelMock from './Issue35MyPageReadablePanelMock.vue';
import Issue35ReservationPanelMock from './Issue35ReservationPanelMock.vue';
import Issue35SessionCardMock from './Issue35SessionCardMock.vue';
import Issue35TopBarMock from './Issue35TopBarMock.vue';

defineProps<{
  mode: 'default' | 'loading' | 'success' | 'error';
  contentVariant?: 'normal' | 'mixed-fallback' | 'all-fallback' | 'empty' | 'many';
}>();
</script>

<style scoped>
.layout {
  display: grid;
  gap: 12px;
  width: min(980px, 100%);
  padding: 14px;
  border-radius: 16px;
  border: 1px solid #cde8de;
  background: linear-gradient(160deg, #f4fff9, #ffffff 48%, #f8fffd);
  font-family: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
}

.layout__body {
  display: grid;
  gap: 12px;
  grid-template-columns: 1.25fr 1fr;
}

.layout__left,
.layout__right {
  display: grid;
  gap: 10px;
  align-content: start;
}

.feedback {
  margin: 0;
  padding: 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
}

.feedback--loading {
  background: #fff6df;
  color: #8c5a00;
}

.feedback--success {
  background: #e8fbef;
  color: #1e6a45;
}

.feedback--error {
  background: #ffe8e8;
  color: #9f2d2d;
}

.layout--loading {
  border-color: #e9d09a;
}

.layout--success {
  border-color: #9bd7b4;
}

.layout--error {
  border-color: #e6a5a5;
}

@media (max-width: 900px) {
  .layout {
    width: min(390px, 100%);
    padding: 12px;
  }

  .layout__body {
    grid-template-columns: 1fr;
  }
}
</style>
