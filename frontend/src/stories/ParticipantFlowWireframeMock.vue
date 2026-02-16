<template>
  <article class="participant-ui" :class="`participant-ui--${status}`">
    <header class="participant-ui__hero">
      <p class="participant-ui__route">{{ routeLabel }}</p>
      <h3 class="participant-ui__title">{{ title }}</h3>
      <p class="participant-ui__summary">{{ summary }}</p>
      <div class="participant-ui__chips">
        <span v-for="item in participantFeatures" :key="`on-${item}`" class="chip chip--on">{{
          item
        }}</span>
      </div>
      <div class="participant-ui__chips participant-ui__chips--off">
        <span v-for="item in adminFeatures" :key="`off-${item}`" class="chip chip--off">{{
          item
        }}</span>
      </div>
    </header>

    <section class="participant-ui__panels">
      <section class="panel panel--sessions" aria-label="セッション一覧モック">
        <h4>セッション一覧</h4>
        <ul class="session-list">
          <li v-for="item in sessionCards" :key="item.id" class="session-card">
            <p class="session-card__time">{{ item.time }} | {{ item.track }}</p>
            <p class="session-card__title">{{ item.title }}</p>
            <button type="button" :disabled="status === 'loading' || item.reserved">
              {{ item.cta }}
            </button>
          </li>
        </ul>
      </section>

      <section class="panel panel--reservations" aria-label="予約一覧モック">
        <h4>予約一覧</h4>
        <ul class="reservation-list">
          <li v-for="item in reservations" :key="item.id" class="reservation-row">
            <p>{{ item.title }}</p>
            <span>{{ item.state }}</span>
          </li>
        </ul>
      </section>

      <section class="panel panel--mypage" aria-label="マイページモック">
        <h4>マイページ</h4>
        <div class="qr-placeholder" aria-hidden="true">
          <span>QR</span>
        </div>
        <p class="mypage-text">{{ myPageNote }}</p>
      </section>
    </section>

    <footer class="participant-ui__footer">
      <p class="participant-ui__handoff">引き渡し先: {{ handoffIssue }}</p>
      <button type="button" class="participant-ui__primary" :disabled="status === 'loading'">
        {{ actionLabel }}
      </button>
      <p v-if="status === 'loading'" class="feedback feedback--loading">
        予約情報を同期しています...
      </p>
      <p v-if="status === 'success'" class="feedback feedback--success">
        予約とマイページ表示を更新しました
      </p>
      <p v-if="status === 'error'" class="feedback feedback--error">
        予約操作に失敗しました。接続を確認して再試行してください
      </p>
    </footer>
  </article>
</template>

<script setup lang="ts">
type SessionCard = {
  id: string;
  time: string;
  track: string;
  title: string;
  cta: string;
  reserved?: boolean;
};

type ReservationRow = {
  id: string;
  title: string;
  state: string;
};

withDefaults(
  defineProps<{
    routeLabel: '/participant';
    title: string;
    summary: string;
    participantFeatures: string[];
    adminFeatures: string[];
    sessionCards: SessionCard[];
    reservations: ReservationRow[];
    myPageNote: string;
    handoffIssue: '#39';
    actionLabel: string;
    status?: 'default' | 'loading' | 'success' | 'error';
  }>(),
  {
    status: 'default',
  },
);
</script>

<style scoped>
.participant-ui {
  --ui-font-display: 'Space Grotesk', 'Avenir Next', 'Hiragino Kaku Gothic ProN', sans-serif;
  --ui-font-body: 'IBM Plex Sans JP', 'Noto Sans JP', sans-serif;
  display: grid;
  gap: 18px;
  width: min(980px, 100%);
  padding: clamp(16px, 3vw, 28px);
  border-radius: 22px;
  border: 1px solid #d0efe0;
  background:
    radial-gradient(circle at 85% 5%, #d8fff0 0%, rgba(216, 255, 240, 0) 36%),
    linear-gradient(140deg, #f5fff8 0%, #ffffff 52%, #f8fffd 100%);
  color: #103225;
  font-family: var(--ui-font-body);
  box-shadow: 0 12px 36px rgba(15, 76, 52, 0.12);
}

.participant-ui__hero {
  display: grid;
  gap: 10px;
}

.participant-ui__route {
  width: fit-content;
  margin: 0;
  padding: 4px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-size: 12px;
  font-weight: 700;
}

.participant-ui__title {
  margin: 0;
  font-family: var(--ui-font-display);
  font-size: clamp(22px, 3vw, 30px);
  letter-spacing: 0.02em;
}

.participant-ui__summary {
  margin: 0;
  font-size: 15px;
  line-height: 1.6;
}

.participant-ui__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.participant-ui__chips--off {
  padding-top: 4px;
}

.chip {
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 600;
}

.chip--on {
  background: #e8fff1;
  color: #13663f;
  border: 1px solid #9ae6be;
}

.chip--off {
  background: #fff2f2;
  color: #8d2323;
  border: 1px solid #f6b4b4;
}

.participant-ui__panels {
  display: grid;
  grid-template-columns: 1.35fr 1fr 0.9fr;
  gap: 12px;
}

.panel {
  display: grid;
  gap: 10px;
  padding: 14px;
  border-radius: 14px;
  background: #ffffff;
  border: 1px solid #ddf4e9;
}

.panel h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 700;
}

.session-list,
.reservation-list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

.session-card {
  display: grid;
  gap: 4px;
  padding: 10px;
  border-radius: 12px;
  background: #f7fffb;
  border: 1px solid #d8f6e7;
}

.session-card__time,
.session-card__title {
  margin: 0;
}

.session-card__time {
  font-size: 12px;
  color: #4c7d66;
}

.session-card__title {
  font-size: 14px;
  font-weight: 600;
}

.session-card button,
.participant-ui__primary {
  height: 36px;
  border: none;
  border-radius: 10px;
  padding: 0 14px;
  font-size: 13px;
  font-weight: 700;
  font-family: var(--ui-font-body);
  background: linear-gradient(120deg, #1ea568, #157b4d);
  color: #ffffff;
}

.session-card button[disabled],
.participant-ui__primary[disabled] {
  cursor: not-allowed;
  opacity: 0.6;
}

.reservation-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px;
  border-radius: 10px;
  background: #f8fcff;
  border: 1px solid #ddebf5;
}

.reservation-row p,
.reservation-row span {
  margin: 0;
  font-size: 12px;
}

.qr-placeholder {
  width: 110px;
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  border: 2px dashed #2f7a5a;
  border-radius: 10px;
  font-family: var(--ui-font-display);
  font-size: 30px;
  color: #2f7a5a;
  background: repeating-linear-gradient(-45deg, #f4fff8, #f4fff8 8px, #edfbf3 8px, #edfbf3 16px);
}

.mypage-text {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: #496d5a;
}

.participant-ui__footer {
  display: grid;
  gap: 8px;
  border-top: 1px solid #d9efe4;
  padding-top: 12px;
}

.participant-ui__handoff {
  margin: 0;
  font-size: 12px;
  color: #4a7c65;
}

.feedback {
  margin: 0;
  font-size: 13px;
  font-weight: 600;
}

.feedback--loading {
  color: #8b5c07;
}

.feedback--success {
  color: #166534;
}

.feedback--error {
  color: #b91c1c;
}

.participant-ui--loading {
  border-color: #f1d49b;
}

.participant-ui--success {
  border-color: #98dfb6;
}

.participant-ui--error {
  border-color: #efadad;
}

@media (max-width: 900px) {
  .participant-ui__panels {
    grid-template-columns: 1fr;
  }

  .qr-placeholder {
    width: 96px;
  }
}
</style>
