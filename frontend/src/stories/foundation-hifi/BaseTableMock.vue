<template>
  <div class="ui-table-wrap">
    <table class="ui-table">
      <thead>
        <tr>
          <th scope="col" class="ui-table__col--id">ID</th>
          <th scope="col" class="ui-table__col--title">Title</th>
          <th scope="col" class="ui-table__col--action">Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="row in rows" :key="row.id">
          <td class="ui-table__col--id">{{ row.id }}</td>
          <td class="ui-table__col--title">{{ row.title }}</td>
          <td class="ui-table__col--action">
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
  border-radius: var(--radius-lg);
  background: var(--semantic-color-bg-surface);
}

.ui-table {
  width: 100%;
  border-collapse: collapse;
}

.ui-table th,
.ui-table td {
  padding: var(--space-2) var(--space-3);
  text-align: left;
  border-bottom: 1px solid var(--semantic-color-border-default);
  font-size: var(--font-size-sm);
}

.ui-table th {
  color: var(--semantic-color-text-secondary);
  background: var(--semantic-color-bg-subtle);
}

.ui-table__col--id {
  min-width: 7rem;
  white-space: nowrap;
}

.ui-table__col--action {
  min-width: 8rem;
  white-space: nowrap;
}

.ui-table__col--title {
  min-width: 14rem;
}

@media (max-width: 767px) {
  .ui-table__col--title {
    max-width: 10rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
