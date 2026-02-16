<template>
  <article class="wireframe-card" :data-status="status">
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <button type="button" :disabled="status === 'loading'">
      {{ status === 'loading' ? '読み込み中...' : ctaLabel }}
    </button>
  </article>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    title: string;
    description: string;
    ctaLabel: string;
    status?: 'default' | 'loading' | 'success' | 'error';
  }>(),
  {
    status: 'default',
  },
);
</script>

<style scoped>
.wireframe-card {
  display: grid;
  gap: 12px;
  max-width: 320px;
  padding: 16px;
  border: 1px solid #d0d7de;
  border-radius: 8px;
  background: #fff;
}

.wireframe-card[data-status='success'] {
  border-color: #1a7f37;
}

.wireframe-card[data-status='error'] {
  border-color: #cf222e;
}

.wireframe-card button {
  width: fit-content;
  padding: 8px 14px;
  border: 1px solid #24292f;
  border-radius: 6px;
  background: #f6f8fa;
}
</style>
