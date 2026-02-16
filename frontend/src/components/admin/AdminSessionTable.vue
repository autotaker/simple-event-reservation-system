<template>
  <section class="table-wrap">
    <header class="table-wrap__header">
      <h3>管理対象セッション</h3>
      <div class="table-wrap__actions">
        <span>{{ sessions.length }}件</span>
        <button type="button" :disabled="disabled" @click="$emit('load')">管理一覧を取得</button>
      </div>
    </header>

    <table v-if="sessions.length > 0">
      <thead>
        <tr>
          <th scope="col">ID</th>
          <th scope="col">開始時刻</th>
          <th scope="col">トラック</th>
          <th scope="col">タイトル</th>
          <th scope="col">定員</th>
          <th scope="col">予約数</th>
          <th scope="col">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="session in sessions" :key="session.sessionId">
          <td>{{ session.sessionId }}</td>
          <td>{{ session.startTime }}</td>
          <td>{{ session.track }}</td>
          <td>{{ session.title }}</td>
          <td>{{ session.capacity }}</td>
          <td>{{ session.reservedCount }}</td>
          <td>
            <button type="button" :disabled="disabled" @click="$emit('edit', session)">編集</button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup lang="ts">
import type { AdminSession } from '../../composables/useReservationApp';

defineProps<{
  sessions: AdminSession[];
  disabled: boolean;
}>();

defineEmits<{
  load: [];
  edit: [session: AdminSession];
}>();
</script>

<style scoped>
.table-wrap {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-warning);
  background: var(--semantic-color-bg-surface);
}

.table-wrap__header,
.table-wrap__actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.table-wrap__header h3,
.table-wrap__actions span {
  margin: 0;
}
</style>
