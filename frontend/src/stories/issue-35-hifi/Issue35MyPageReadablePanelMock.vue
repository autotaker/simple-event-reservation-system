<template>
  <section class="mypage">
    <header>
      <h4>マイページ</h4>
      <button type="button">更新</button>
    </header>

    <div class="qr" aria-hidden="true">QR</div>

    <ul v-if="mode === 'default' || mode === 'success'" class="list">
      <li>
        <p class="title">Keynote: Product Vision</p>
        <span>10:00 | Track A</span>
      </li>
      <li>
        <p class="title">API Design Clinic</p>
        <span>11:30 | Track B</span>
      </li>
    </ul>

    <ul v-else-if="mode === 'fallback'" class="list">
      <li>
        <p class="title">不明なセッション</p>
        <span>更新して最新情報を取得してください</span>
      </li>
    </ul>

    <p v-if="mode === 'loading'" class="status status--loading">予約情報を更新中です...</p>
    <p v-if="mode === 'success'" class="status status--success">予約情報を更新しました。</p>
    <p v-if="mode === 'error'" class="status status--error">予約情報の取得に失敗しました。</p>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  mode: 'default' | 'loading' | 'success' | 'error' | 'fallback';
}>();
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

.list {
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;
}

.list li {
  display: grid;
  gap: 2px;
  padding: 8px;
  border-radius: 10px;
  background: #f6fffa;
}

.title {
  font-size: 13px;
  font-weight: 700;
  color: #123c2d;
}

span {
  font-size: 11px;
  color: #3f6758;
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
