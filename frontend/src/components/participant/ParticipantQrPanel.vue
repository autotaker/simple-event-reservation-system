<template>
  <section class="qr-panel ui-panel">
    <header>
      <h2>マイページ</h2>
      <button
        class="ui-button ui-button--secondary"
        type="button"
        :disabled="disabled || !hasToken"
        @click="$emit('refresh')"
      >
        更新
      </button>
    </header>

    <img
      v-if="qrCodePayload && qrCodeImageUrl"
      :src="qrCodeImageUrl"
      :data-qr-payload="qrCodePayload"
      alt="受付用QRコード"
      width="180"
      height="180"
    />
    <div v-else class="qr" aria-hidden="true">QR</div>

    <p>
      {{
        qrCodePayload && qrCodeImageUrl
          ? '受付用QRコードを表示中'
          : qrCodePayload && qrCodeGenerationStatus === 'error'
            ? '受付用QRコードを生成できませんでした。更新してください。'
            : qrCodePayload
              ? '受付用QRコードを生成中です...'
              : 'ログイン後に受付QRコードが表示されます。'
      }}
    </p>

    <ul v-if="reservations.length > 0" class="ui-list">
      <li v-for="reservation in reservations" :key="reservation" class="ui-list-item">
        {{ reservation }}
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
defineProps<{
  qrCodePayload: string;
  qrCodeImageUrl: string;
  qrCodeGenerationStatus: 'idle' | 'generating' | 'ready' | 'error';
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
  justify-items: center;
  gap: 10px;
  border: 1px solid var(--semantic-color-participant-panel-qr-border);
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

.qr-panel :deep(.ui-button) {
  min-height: var(--semantic-component-participant-button-secondary-height);
  border-radius: var(--semantic-component-participant-button-secondary-radius);
  background: var(--semantic-color-participant-action-secondary-bg);
  color: var(--semantic-color-participant-action-secondary-text);
  font-size: var(--semantic-component-participant-button-text-size);
  font-weight: var(--semantic-component-participant-button-text-weight);
}

.qr {
  width: var(--semantic-component-participant-qr-size);
  aspect-ratio: 1;
  display: grid;
  place-items: center;
  border-radius: var(--semantic-component-participant-qr-radius);
  border: var(--semantic-component-participant-qr-border-width) dashed
    var(--semantic-color-participant-panel-qr-accent);
  font-size: 28px;
  font-weight: 700;
  color: var(--semantic-color-participant-panel-qr-accent);
  background: repeating-linear-gradient(
    -45deg,
    var(--semantic-color-participant-panel-qr-pattern-a),
    var(--semantic-color-participant-panel-qr-pattern-a) 7px,
    var(--semantic-color-participant-panel-qr-pattern-b) 7px,
    var(--semantic-color-participant-panel-qr-pattern-b) 14px
  );
}

p {
  font-size: 12px;
  color: var(--semantic-color-participant-panel-qr-label);
}

.qr-panel :deep(.ui-list) {
  width: 100%;
}

.qr-panel :deep(.ui-list-item) {
  background: var(--semantic-color-participant-panel-list-item-bg);
  font-size: 12px;
}
</style>
