<template>
  <header class="top-bar">
    <p class="top-bar__event">Tokyo Product Summit 2026</p>
    <div>
      <h3>参加者画面</h3>
      <small>2/22 (Sun) Day 1</small>
    </div>
    <span class="top-bar__guest">Guest A12</span>
  </header>
</template>

<style scoped>
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid #cde8de;
  background: #f5fff9;
}

.top-bar__event {
  margin: 0;
  font-size: 11px;
  color: #1f6650;
}

h3,
small {
  margin: 0;
  display: block;
}

h3 {
  font-size: 14px;
}

small {
  font-size: 11px;
  color: #4b6f5f;
}

.top-bar__guest {
  padding: 4px 8px;
  border-radius: 999px;
  background: #e8fbef;
  color: #1d5f43;
  font-size: 11px;
  font-weight: 700;
}
</style>
