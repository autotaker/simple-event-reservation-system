<template>
  <form class="editor" @submit.prevent="$emit('submit')">
    <h3>{{ heading }}</h3>
    <div class="editor__grid">
      <label>
        タイトル
        <input v-model="title" type="text" required />
      </label>
      <label>
        開始時刻
        <input v-model="startTime" type="time" required />
      </label>
      <label>
        トラック
        <input v-model="track" type="text" required />
      </label>
      <label>
        定員
        <input v-model="capacity" type="number" min="1" required />
      </label>
    </div>
    <div class="editor__actions">
      <button type="submit" :disabled="!canSubmit">{{ submitLabel }}</button>
      <button v-if="showCancel" type="button" @click="$emit('cancel')">キャンセル</button>
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
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid var(--semantic-color-state-warning);
  background: var(--semantic-color-bg-surface);
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

.editor label {
  display: grid;
  gap: 4px;
  font-size: 12px;
  color: var(--semantic-color-text-secondary);
}

.editor__actions {
  display: flex;
  gap: 8px;
}

@media (max-width: 900px) {
  .editor__grid {
    grid-template-columns: 1fr;
  }
}
</style>
