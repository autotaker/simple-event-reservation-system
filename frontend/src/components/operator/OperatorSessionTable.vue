<template>
  <section class="operator-session-table">
    <h2>セッション一覧</h2>
    <button type="button" :disabled="disabled || !hasToken" @click="$emit('refresh')">
      セッション一覧を取得
    </button>
    <p v-if="hasToken && sessions.length === 0">セッション一覧は未取得です。</p>

    <table v-else-if="sessions.length > 0">
      <thead>
        <tr>
          <th scope="col">開始時刻</th>
          <th scope="col">トラック</th>
          <th scope="col">セッション</th>
          <th scope="col">残席ステータス</th>
          <th scope="col">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="session in sessions" :key="session.sessionId">
          <td>{{ session.startTime }}</td>
          <td>{{ session.track }}</td>
          <td>{{ session.title }}</td>
          <td>{{ session.availabilityStatusLabel }}</td>
          <td>
            <button
              type="button"
              :disabled="disabled || !hasToken || session.unavailable || session.reserved"
              @click="$emit('reserve', session.sessionId, session.title)"
            >
              {{ session.reserved ? '予約済み' : '予約する' }}
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </section>
</template>

<script setup lang="ts">
export type OperatorSessionRow = {
  sessionId: string;
  title: string;
  startTime: string;
  track: string;
  availabilityStatusLabel: string;
  reserved: boolean;
  unavailable: boolean;
};

defineProps<{
  hasToken: boolean;
  disabled?: boolean;
  sessions: OperatorSessionRow[];
}>();

defineEmits<{
  refresh: [];
  reserve: [sessionId: string, title: string];
}>();
</script>

<style scoped>
.operator-session-table {
  display: grid;
  gap: 10px;
}

.operator-session-table h2,
.operator-session-table p {
  margin: 0;
}
</style>
