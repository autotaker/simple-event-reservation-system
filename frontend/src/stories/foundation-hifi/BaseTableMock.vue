<template>
  <div class="ui-table-wrap">
    <table class="ui-table">
      <thead>
        <tr>
          <th scope="col">ID</th>
          <th scope="col">Title</th>
          <th scope="col">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row.id">
          <td>{{ row.id }}</td>
          <td>{{ row.title }}</td>
          <td>
            <BaseButtonMock :label="row.action" tone="secondary" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import BaseButtonMock from './BaseButtonMock.vue';

defineProps<{
  rows: Array<{ id: string; title: string; action: string }>;
}>();
</script>

<style scoped>
.ui-table-wrap {
  overflow-x: auto;
  border: 1px solid var(--semantic-color-border-default);
  border-radius: 12px;
  background: var(--semantic-color-bg-surface);
}

.ui-table {
  width: 100%;
  border-collapse: collapse;
}

.ui-table th,
.ui-table td {
  padding: 10px;
  text-align: left;
  border-bottom: 1px solid var(--semantic-color-border-default);
  font-size: 13px;
}

.ui-table th {
  color: var(--semantic-color-text-secondary);
  background: var(--semantic-color-bg-subtle);
}
</style>
