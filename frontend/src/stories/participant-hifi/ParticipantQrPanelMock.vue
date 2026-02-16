<template>
  <section class="qr-panel">
    <h4>マイページ</h4>
    <div class="qr" aria-hidden="true">QR</div>
    <p>{{ caption }}</p>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  caption: string;
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

h4,
p {
  margin: 0;
}

h4 {
  justify-self: start;
  font-size: 14px;
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
</style>
