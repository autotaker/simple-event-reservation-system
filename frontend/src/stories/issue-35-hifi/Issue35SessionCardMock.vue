<template>
  <article class="card">
    <p class="card__meta">11:30 | Track B</p>
    <h4>API Design Clinic</h4>
    <p class="card__desc">既存の予約一覧は可読表示を維持。キャンセル導線は右パネルで継続。</p>
  </article>
</template>

<style scoped>
.card {
  display: grid;
  gap: 6px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #d9e8f4;
  background: #ffffff;
}

.card__meta,
.card__desc,
h4 {
  margin: 0;
}

.card__meta {
  font-size: 11px;
  color: #4f657f;
}

h4 {
  font-size: 14px;
}

.card__desc {
  font-size: 12px;
  color: #546170;
}
</style>
