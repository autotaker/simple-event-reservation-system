<template>
  <section class="csv-panel">
    <h3>CSV出力</h3>
    <p>集計前に最新の予約状態へ同期してください。</p>
    <div class="csv-panel__actions">
      <button type="button" :disabled="disabled">予約CSVを出力</button>
      <button type="button" :disabled="disabled">チェックインCSVを出力</button>
    </div>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  disabled?: boolean;
}>();
</script>

<style scoped>
.csv-panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #efd59b;
  background: #fffef8;
}

.csv-panel h3,
.csv-panel p {
  margin: 0;
}

.csv-panel p {
  font-size: 12px;
  color: #6b4b0f;
}

.csv-panel__actions {
  display: grid;
  gap: 8px;
}

.csv-panel button {
  height: 34px;
  border: none;
  border-radius: 10px;
  background: #92400e;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
}

.csv-panel button:disabled {
  background: #c3ab84;
  cursor: not-allowed;
}
</style>
