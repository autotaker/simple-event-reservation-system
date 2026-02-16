<template>
  <section class="denied">
    <p class="denied__code">403 Forbidden</p>
    <h3>管理導線へのアクセス権限がありません</h3>
    <p class="denied__reason">理由: このアカウントは参加者権限のため、管理機能は利用できません。</p>
    <button type="button">参加者トップへ戻る</button>
  </section>
</template>

<style scoped>
.denied {
  display: grid;
  gap: 8px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-danger);
  background: var(--semantic-color-state-danger-soft);
}

.denied__code,
.denied h3,
.denied__reason {
  margin: 0;
}

.denied__code {
  font-size: 12px;
  font-weight: 700;
  color: var(--semantic-color-state-danger);
}

.denied h3 {
  font-size: 14px;
  color: var(--semantic-color-text-primary);
}

.denied__reason {
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
  line-height: 1.4;
}

.denied button {
  width: fit-content;
  min-width: 150px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: var(--semantic-component-button-bg);
  color: var(--semantic-component-button-text);
  font-size: 12px;
  font-weight: 700;
}
</style>
