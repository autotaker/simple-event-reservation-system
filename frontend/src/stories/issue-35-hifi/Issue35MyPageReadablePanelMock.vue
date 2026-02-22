<template>
  <section class="mypage">
    <header>
      <h4>マイページ</h4>
      <button type="button">更新</button>
    </header>

    <div class="qr" aria-hidden="true">QR</div>

    <p class="format-rule">表示形式: HH:mm | Track X</p>

    <p v-if="reservations.length === 0" class="empty">予約はありません。</p>

    <ul v-else class="list" :class="{ 'list--many': variant === 'many' }">
      <li
        v-for="reservation in reservations"
        :key="reservation.id"
        :class="{ 'is-fallback': reservation.fallback }"
      >
        <p class="title">{{ reservation.title }}</p>
        <span>{{ reservation.meta }}</span>
      </li>
    </ul>

    <p v-if="mode === 'loading'" class="status status--loading">予約情報を更新中です...</p>
    <p v-if="mode === 'success'" class="status status--success">予約情報を更新しました。</p>
    <p v-if="mode === 'error'" class="status status--error">予約情報の取得に失敗しました。</p>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue';

type ReservationItem = {
  id: string;
  title: string;
  meta: string;
  fallback?: boolean;
};

const props = defineProps<{
  mode: 'default' | 'loading' | 'success' | 'error';
  variant?: 'normal' | 'mixed-fallback' | 'all-fallback' | 'empty' | 'many';
}>();

const reservations = computed<ReservationItem[]>(() => {
  if (props.variant === 'empty') {
    return [];
  }

  if (props.variant === 'all-fallback') {
    return [
      {
        id: 'fallback-1',
        title: '不明なセッション',
        meta: '更新して最新情報を取得してください',
        fallback: true,
      },
      {
        id: 'fallback-2',
        title: '不明なセッション',
        meta: '更新して最新情報を取得してください',
        fallback: true,
      },
    ];
  }

  if (props.variant === 'mixed-fallback') {
    return [
      { id: 'keynote', title: 'Keynote: Product Vision', meta: '10:00 | Track A' },
      {
        id: 'unknown-1',
        title: '不明なセッション',
        meta: '更新して最新情報を取得してください',
        fallback: true,
      },
      { id: 'session-a1', title: 'API Design Clinic', meta: '11:30 | Track B' },
    ];
  }

  if (props.variant === 'many') {
    return [
      { id: 'r1', title: 'Keynote: Product Vision', meta: '10:00 | Track A' },
      { id: 'r2', title: 'API Design Clinic', meta: '11:30 | Track B' },
      { id: 'r3', title: 'Design QA Loop', meta: '13:00 | Track C' },
      { id: 'r4', title: 'Prompt Engineering', meta: '14:30 | Track A' },
      { id: 'r5', title: 'Closing Session', meta: '16:00 | Track B' },
    ];
  }

  return [
    { id: 'keynote', title: 'Keynote: Product Vision', meta: '10:00 | Track A' },
    { id: 'session-a1', title: 'API Design Clinic', meta: '11:30 | Track B' },
  ];
});
</script>

<style scoped>
.mypage {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #d8efe3;
  background: #ffffff;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h4,
p,
span {
  margin: 0;
}

h4 {
  font-size: 14px;
}

button {
  border: 0;
  border-radius: 8px;
  background: #2f6f57;
  color: #ffffff;
  font-size: 12px;
  height: 30px;
  padding: 0 10px;
}

.qr {
  width: 108px;
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  border-radius: 10px;
  border: 2px dashed #2a7758;
  color: #2a7758;
  font-size: 28px;
  font-weight: 700;
  background: repeating-linear-gradient(-45deg, #f5fff9, #f5fff9 7px, #ecfbf3 7px, #ecfbf3 14px);
}

.format-rule {
  font-size: 11px;
  color: #335f4f;
}

.empty {
  font-size: 12px;
  color: #3f6758;
  padding: 6px 8px;
  border-radius: 8px;
  background: #f6fffa;
}

.list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

.list--many {
  max-height: 180px;
  overflow-y: auto;
}

.list li {
  display: grid;
  gap: 2px;
  padding: 8px;
  border-radius: 10px;
  background: #f6fffa;
}

.list li.is-fallback {
  background: #fff7ed;
}

.title {
  font-size: 13px;
  font-weight: 700;
  color: #123c2d;
}

.is-fallback .title {
  color: #7c2d12;
}

span {
  font-size: 11px;
  color: #3f6758;
}

.is-fallback span {
  color: #9a3412;
}

.status {
  padding: 8px 10px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 700;
}

.status--loading {
  background: #fff6df;
  color: #8c5a00;
}

.status--success {
  background: #e8fbef;
  color: #1e6a45;
}

.status--error {
  background: #ffe8e8;
  color: #9f2d2d;
}
</style>
