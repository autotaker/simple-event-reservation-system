<template>
  <section class="reservation-panel">
    <header>
      <h2>予約一覧</h2>
      <button type="button" :disabled="disabled || !hasToken" @click="$emit('refresh')">
        更新
      </button>
    </header>

    <button type="button" :disabled="disabled || !hasToken" @click="$emit('reserveKeynote')">
      キーノートを予約
    </button>

    <p v-if="registered">参加登録: 完了</p>
    <p v-else-if="hasToken && registrationStatusLoaded">参加登録: 未完了</p>

    <ul v-if="reservations.length > 0">
      <li v-for="item in reservations" :key="item.id">
        <p>{{ item.title }}</p>
        <span>{{ item.time }}</span>
        <button type="button" :disabled="disabled || !hasToken" @click="$emit('cancel', item.id)">
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
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-participant-panel-reservation-border);
  background: var(--semantic-color-participant-panel-surface);
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

button {
  height: var(--semantic-component-participant-button-secondary-height);
  padding: 0 var(--semantic-component-participant-button-padding-x);
  border: none;
  border-radius: var(--semantic-component-participant-button-secondary-radius);
  background: var(--semantic-color-participant-action-secondary-bg);
  color: var(--semantic-color-participant-action-secondary-text);
  font-size: var(--semantic-component-participant-button-text-size);
  font-weight: var(--semantic-component-participant-button-text-weight);
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

ul {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

li {
  display: grid;
  gap: 4px;
  padding: 8px;
  border-radius: 10px;
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
