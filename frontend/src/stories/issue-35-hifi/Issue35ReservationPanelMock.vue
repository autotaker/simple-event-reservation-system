<template>
  <section class="panel">
    <header>
      <h4>予約一覧</h4>
      <button type="button">更新</button>
    </header>

    <ul>
      <li>
        <p>Keynote: Product Vision</p>
        <span>10:00 | Track A</span>
        <button type="button" class="ghost">キャンセル</button>
      </li>
      <li>
        <p>API Design Clinic</p>
        <span>11:30 | Track B</span>
        <button type="button" class="ghost">キャンセル</button>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.panel {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid #d9e8f4;
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
  background: #315e96;
  color: #ffffff;
  font-size: 12px;
  height: 30px;
  padding: 0 10px;
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
  background: #f8fbff;
}

span {
  font-size: 11px;
  color: #4f657f;
}

.ghost {
  justify-self: start;
  height: 26px;
  background: #ffffff;
  color: #315e96;
  border: 1px solid #9db7d4;
}
</style>
