<template>
  <section class="reservation-panel ui-panel">
    <header>
      <h2>予約一覧</h2>
      <button
        class="ui-button ui-button--secondary"
        type="button"
        :disabled="disabled || !hasToken"
        @click="$emit('refresh')"
      >
        更新
      </button>
    </header>

    <button
      class="ui-button ui-button--secondary"
      type="button"
      :disabled="disabled || !hasToken"
      @click="$emit('reserveKeynote')"
    >
      キーノートを予約
    </button>

    <p v-if="registered">参加登録: 完了</p>
    <p v-else-if="hasToken && registrationStatusLoaded">参加登録: 未完了</p>

    <ul v-if="reservations.length > 0" class="ui-list">
      <li v-for="item in reservations" :key="item.id" class="ui-list-item">
        <p>{{ item.title }}</p>
        <span>{{ item.time }}</span>
        <button
          class="ui-button ui-button--secondary"
          type="button"
          :disabled="disabled || !hasToken"
          @click="$emit('cancel', item.id)"
        >
          キャンセル
        </button>
      </li>
    </ul>

    <p v-else-if="hasToken">予約はありません。</p>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  reservations: { id: string; title: string; time: string }[];
  disabled?: boolean;
  hasToken: boolean;
  registered: boolean;
  registrationStatusLoaded: boolean;
}>();

defineEmits<{
  refresh: [];
  reserveKeynote: [];
  cancel: [reservationId: string];
}>();
</script>

<style scoped>
.reservation-panel {
  gap: 10px;
  border: 1px solid var(--semantic-color-participant-panel-reservation-border);
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h2 {
  margin: 0;
  font-size: 14px;
}

.reservation-panel :deep(.ui-button) {
  min-height: var(--semantic-component-participant-button-secondary-height);
  border-radius: var(--semantic-component-participant-button-secondary-radius);
  background: var(--semantic-color-participant-action-secondary-bg);
  color: var(--semantic-color-participant-action-secondary-text);
  font-size: var(--semantic-component-participant-button-text-size);
  font-weight: var(--semantic-component-participant-button-text-weight);
}

.reservation-panel :deep(.ui-list-item) {
  gap: 4px;
  background: var(--semantic-color-participant-panel-list-item-bg);
}

p,
span {
  margin: 0;
}

span {
  font-size: 11px;
  color: var(--semantic-color-participant-panel-list-item-meta);
}
</style>
