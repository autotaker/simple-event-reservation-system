<template>
  <form class="editor ui-panel" @submit.prevent="$emit('submit')">
    <h3>{{ heading }}</h3>
    <div class="editor__grid">
      <label class="ui-field">
        <span class="ui-field__label">タイトル</span>
        <input v-model="title" class="ui-field__input" type="text" required />
      </label>
      <label class="ui-field">
        <span class="ui-field__label">開始時刻</span>
        <input v-model="startTime" class="ui-field__input" type="time" required />
      </label>
      <label class="ui-field">
        <span class="ui-field__label">トラック</span>
        <input v-model="track" class="ui-field__input" type="text" required />
      </label>
      <label class="ui-field">
        <span class="ui-field__label">定員</span>
        <input v-model="capacity" class="ui-field__input" type="number" min="1" required />
      </label>
    </div>
    <div class="editor__actions">
      <button class="ui-button ui-button--warning" type="submit" :disabled="!canSubmit">
        {{ submitLabel }}
      </button>
      <button
        v-if="showCancel"
        class="ui-button ui-button--secondary"
        type="button"
        @click="$emit('cancel')"
      >
        キャンセル
      </button>
    </div>
  </form>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { AdminForm } from '../../composables/useReservationApp';

const props = defineProps<{
  heading: string;
  form: AdminForm;
  submitLabel: string;
  canSubmit: boolean;
  showCancel?: boolean;
}>();

const emit = defineEmits<{
  submit: [];
  cancel: [];
  'update:form': [form: AdminForm];
}>();

const updateForm = (patch: Partial<AdminForm>): void => {
  emit('update:form', { ...props.form, ...patch });
};

const title = computed({
  get: () => props.form.title,
  set: (value: string) => updateForm({ title: value }),
});

const startTime = computed({
  get: () => props.form.startTime,
  set: (value: string) => updateForm({ startTime: value }),
});

const track = computed({
  get: () => props.form.track,
  set: (value: string) => updateForm({ track: value }),
});

const capacity = computed({
  get: () => props.form.capacity,
  set: (value: string) => updateForm({ capacity: value }),
});
</script>

<style scoped>
.editor {
  gap: 10px;
  border: 1px solid var(--semantic-color-state-warning);
}

.editor h3 {
  margin: 0;
  font-size: 14px;
}

.editor__grid {
  display: grid;
  gap: 8px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.editor__actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

@media (max-width: 900px) {
  .editor__grid {
    grid-template-columns: 1fr;
  }
}
</style>
