<template>
  <section class="csv-panel ui-panel">
    <h3>CSV出力</h3>
    <p>集計前に最新の予約状態へ同期してください。</p>
    <div class="csv-panel__actions">
      <button
        class="ui-button ui-button--warning"
        type="button"
        :disabled="disabled"
        @click="$emit('downloadReservations')"
      >
        予約一覧CSVを出力
      </button>
      <button
        class="ui-button ui-button--warning"
        type="button"
        :disabled="disabled"
        @click="$emit('downloadCheckIns')"
      >
        チェックインCSVを出力
      </button>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  disabled: boolean;
}>();

defineEmits<{
  downloadReservations: [];
  downloadCheckIns: [];
}>();
</script>

<style scoped>
.csv-panel {
  gap: 10px;
  border: 1px solid var(--semantic-color-state-warning);
}

.csv-panel h3,
.csv-panel p {
  margin: 0;
}

.csv-panel p {
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.csv-panel__actions {
  display: grid;
  gap: 8px;
}
</style>
