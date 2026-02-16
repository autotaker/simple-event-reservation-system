<template>
  <section class="qr-panel">
    <header>
      <h2>マイページ</h2>
      <button type="button" :disabled="disabled || !hasToken" @click="$emit('refresh')">
        更新
      </button>
    </header>

    <img v-if="qrCodePayload" :src="qrCodeImageUrl" alt="受付用QRコード" width="180" height="180" />
    <div v-else class="qr" aria-hidden="true">QR</div>

    <p>
      {{ qrCodePayload ? '受付用QRコードを表示中' : 'ログイン後に受付QRコードが表示されます。' }}
    </p>

    <ul v-if="reservations.length > 0">
      <li v-for="reservation in reservations" :key="reservation">{{ reservation }}</li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  qrCodePayload: string;
  qrCodeImageUrl: string;
  reservations: string[];
  hasToken: boolean;
  disabled?: boolean;
}>();

defineEmits<{
  refresh: [];
}>();
</script>

<style scoped>
.qr-panel {
  display: grid;
  justify-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #d8efe3;
  background: #ffffff;
}

header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

h2,
p {
  margin: 0;
}

h2 {
  font-size: 14px;
}

button {
  height: 30px;
  padding: 0 10px;
  border: none;
  border-radius: 8px;
  background: #315e96;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.qr {
  width: 112px;
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  border-radius: 12px;
  border: 2px dashed #2a7758;
  font-size: 28px;
  font-weight: 700;
  color: #2a7758;
  background: repeating-linear-gradient(-45deg, #f5fff9, #f5fff9 7px, #ecfbf3 7px, #ecfbf3 14px);
}

p {
  font-size: 12px;
  color: #4b6f5f;
}

ul {
  width: 100%;
  margin: 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 6px;
}

li {
  padding: 8px;
  border-radius: 10px;
  background: #f8fbff;
  font-size: 12px;
}
</style>
