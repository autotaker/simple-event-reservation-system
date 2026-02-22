<template>
  <section class="forbidden">
    <p class="forbidden__code">403 Forbidden</p>
    <h3>管理画面を表示できません</h3>
    <p class="forbidden__reason">権限がありません。管理責任者に連絡してください。</p>
    <button type="button">参加者トップへ戻る</button>
  </section>
</template>

<style scoped>
.forbidden {
  display: grid;
  gap: 8px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-danger);
  background: var(--semantic-color-state-danger-soft);
}

.forbidden__code,
.forbidden h3,
.forbidden__reason {
  margin: 0;
}

.forbidden__code {
  font-size: 12px;
  font-weight: 700;
  color: var(--semantic-color-state-danger);
}

.forbidden h3 {
  font-size: 14px;
  color: var(--semantic-color-text-primary);
}

.forbidden__reason {
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
  line-height: 1.4;
}

.forbidden button {
  width: fit-content;
  min-width: 150px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--semantic-color-state-danger);
  color: var(--semantic-color-text-on-primary);
  font-size: 12px;
  font-weight: 700;
}
</style>
