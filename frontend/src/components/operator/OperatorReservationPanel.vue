<template>
  <section class="operator-reservation-panel ui-panel">
    <h2>予約</h2>
    <div class="operator-reservation-panel__actions">
      <button
        class="ui-button ui-button--secondary"
        type="button"
        :disabled="disabled || !hasToken"
        @click="$emit('refresh')"
      >
        予約一覧を取得
      </button>
      <button
        class="ui-button ui-button--secondary"
        type="button"
        :disabled="disabled || !hasToken"
        @click="$emit('reserveKeynote')"
      >
        キーノートを予約
      </button>
    </div>

    <p v-if="registered">参加登録: 完了</p>
    <p v-else-if="hasToken && registrationStatusLoaded">参加登録: 未完了</p>

    <ul class="ui-list">
      <li v-for="reservation in reservations" :key="reservation" class="ui-list-item">
        {{ reservation }}
        <button
          class="ui-button ui-button--secondary"
          type="button"
          :disabled="disabled || !hasToken"
          @click="$emit('cancel', reservation)"
        >
          キャンセル
        </button>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  hasToken: boolean;
  disabled?: boolean;
  reservations: string[];
  registered: boolean;
  registrationStatusLoaded: boolean;
}>();

defineEmits<{
  refresh: [];
  reserveKeynote: [];
  cancel: [sessionId: string];
}>();
</script>

<style scoped>
.operator-reservation-panel {
  gap: 10px;
}

.operator-reservation-panel h2,
.operator-reservation-panel p {
  margin: 0;
}

.operator-reservation-panel__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.operator-reservation-panel :deep(.ui-list-item) {
  grid-template-columns: 1fr auto;
  align-items: center;
}
</style>
