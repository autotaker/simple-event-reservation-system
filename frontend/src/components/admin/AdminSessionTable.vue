<template>
  <section class="table-wrap ui-panel">
    <header class="table-wrap__header">
      <h3>管理対象セッション</h3>
      <div class="table-wrap__actions">
        <span>{{ sessions.length }}件</span>
        <button
          class="ui-button ui-button--warning"
          type="button"
          :disabled="disabled"
          @click="$emit('load')"
        >
          管理一覧を取得
        </button>
      </div>
    </header>

    <div v-if="sessions.length > 0" class="table-wrap__desktop ui-table-wrap">
      <table class="ui-table">
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
              <button
                class="ui-button ui-button--secondary"
                type="button"
                :disabled="disabled"
                @click="$emit('edit', session)"
              >
                編集
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="sessions.length > 0" class="table-wrap__mobile">
      <article
        v-for="session in sessions"
        :key="`mobile-${session.sessionId}`"
        class="table-wrap__mobile-item"
      >
        <header class="table-wrap__mobile-header">
          <h4>{{ session.title }}</h4>
          <span>{{ session.sessionId }}</span>
        </header>
        <dl class="table-wrap__mobile-meta">
          <div>
            <dt>開始時刻</dt>
            <dd>{{ session.startTime }}</dd>
          </div>
          <div>
            <dt>トラック</dt>
            <dd>{{ session.track }}</dd>
          </div>
          <div>
            <dt>定員 / 予約数</dt>
            <dd>{{ session.capacity }} / {{ session.reservedCount }}</dd>
          </div>
        </dl>
        <button
          class="ui-button ui-button--secondary"
          type="button"
          :disabled="disabled"
          @click="$emit('edit', session)"
        >
          編集
        </button>
      </article>
    </div>
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
  gap: 10px;
  border: 1px solid var(--semantic-color-state-warning);
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

.table-wrap__mobile {
  display: none;
}

@media (max-width: 767px) {
  .table-wrap__desktop {
    display: none;
  }

  .table-wrap__mobile {
    display: grid;
    gap: 8px;
  }

  .table-wrap__mobile-item {
    display: grid;
    gap: 8px;
    padding: 10px;
    border-radius: 12px;
    border: 1px solid var(--semantic-color-border-default);
    background: var(--semantic-color-bg-subtle);
  }

  .table-wrap__mobile-header {
    display: grid;
    gap: 4px;
  }

  .table-wrap__mobile-header h4,
  .table-wrap__mobile-header span {
    margin: 0;
  }

  .table-wrap__mobile-header h4 {
    font-size: 14px;
  }

  .table-wrap__mobile-header span {
    font-size: 12px;
    color: var(--semantic-color-text-secondary);
  }

  .table-wrap__mobile-meta {
    margin: 0;
    display: grid;
    gap: 6px;
  }

  .table-wrap__mobile-meta div {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 10px;
  }

  .table-wrap__mobile-meta dt,
  .table-wrap__mobile-meta dd {
    margin: 0;
    font-size: 12px;
  }

  .table-wrap__mobile-meta dt {
    color: var(--semantic-color-text-secondary);
  }
}
</style>
