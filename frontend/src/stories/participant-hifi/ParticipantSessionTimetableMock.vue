<template>
  <section
    class="timetable"
    :class="{ 'timetable--loading': disabled }"
    aria-label="session timetable"
  >
    <header class="timetable__legend">
      <span class="chip chip--open">OPEN</span>
      <span class="chip chip--few">FEW_LEFT</span>
      <span class="chip chip--full">FULL</span>
      <span class="chip chip--reserved">予約済み</span>
    </header>

    <div class="timetable__scroll">
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th v-for="track in tracks" :key="track">{{ track }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in rows" :key="row.time">
            <th>{{ row.time }}</th>
            <td v-for="cell in row.cells" :key="`${row.time}-${cell.track}`">
              <article class="cell" :class="`cell--${cell.state}`">
                <p class="cell__title">{{ cell.title }}</p>
                <p class="cell__meta">{{ cell.label }}</p>
                <button
                  type="button"
                  :disabled="disabled || cell.state === 'full' || cell.state === 'reserved'"
                >
                  {{
                    cell.state === 'reserved'
                      ? '予約済み'
                      : cell.state === 'full'
                        ? '満席'
                        : '予約する'
                  }}
                </button>
              </article>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <p v-if="feedback" class="timetable__feedback" :class="`timetable__feedback--${feedbackTone}`">
      {{ feedback }}
    </p>
  </section>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    disabled?: boolean;
    feedback?: string;
    feedbackTone?: 'loading' | 'success' | 'error';
  }>(),
  {
    disabled: false,
    feedback: '',
    feedbackTone: 'loading',
  },
);

const tracks = ['Track A', 'Track B', 'Track C', 'Track D'];

const rows: Array<{
  time: string;
  cells: Array<{
    track: string;
    title: string;
    state: 'open' | 'few' | 'full' | 'reserved';
    label: string;
  }>;
}> = [
  {
    time: '10:00',
    cells: [
      { track: 'Track A', title: 'Keynote', state: 'open', label: '空席あり' },
      { track: 'Track B', title: 'Design Ops', state: 'few', label: '残りわずか' },
      { track: 'Track C', title: 'Security', state: 'full', label: '満席' },
      { track: 'Track D', title: 'API Clinic', state: 'open', label: '空席あり' },
    ],
  },
  {
    time: '11:00',
    cells: [
      { track: 'Track A', title: 'Infra Talk', state: 'reserved', label: 'あなたの予約' },
      { track: 'Track B', title: 'Frontend', state: 'open', label: '空席あり' },
      { track: 'Track C', title: 'Data', state: 'few', label: '残りわずか' },
      { track: 'Track D', title: 'Product', state: 'open', label: '空席あり' },
    ],
  },
];
</script>

<style scoped>
.timetable {
  display: grid;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  border: 1px solid #c7e8d9;
  background: #ffffff;
}

.timetable--loading {
  opacity: 0.86;
}

.timetable__legend {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  padding: 4px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.chip--open {
  background: #dcfce7;
  color: #166534;
}

.chip--few {
  background: #fef3c7;
  color: #92400e;
}

.chip--full {
  background: #fee2e2;
  color: #991b1b;
}

.chip--reserved {
  background: #cffafe;
  color: #0e7490;
}

.timetable__scroll {
  overflow-x: auto;
}

table {
  width: max(760px, 100%);
  border-collapse: collapse;
  table-layout: fixed;
}

th,
td {
  border: 1px solid #d1e7dc;
  padding: 6px;
  vertical-align: top;
}

th {
  background: #f1f5f9;
  font-size: 12px;
  color: #365a4a;
}

.cell {
  display: grid;
  gap: 6px;
  min-height: 114px;
  padding: 6px;
  border-radius: 10px;
}

.cell--open {
  background: #f0fdf4;
}

.cell--few {
  background: #fffbeb;
}

.cell--full {
  background: #fef2f2;
}

.cell--reserved {
  background: #ecfeff;
}

.cell__title,
.cell__meta {
  margin: 0;
}

.cell__title {
  font-size: 13px;
  font-weight: 700;
  color: #1f3f33;
}

.cell__meta {
  font-size: 11px;
  color: #4f6f61;
}

.cell button {
  height: 30px;
  border: none;
  border-radius: 9px;
  font-size: 12px;
  font-weight: 700;
  background: #1f8e5f;
  color: #ffffff;
}

.cell button:disabled {
  background: #94b4a5;
  cursor: not-allowed;
}

.timetable__feedback {
  margin: 0;
  padding: 8px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 700;
}

.timetable__feedback--loading {
  background: #fff7e0;
  color: #8c5a00;
}

.timetable__feedback--success {
  background: #e8fbef;
  color: #1e6a45;
}

.timetable__feedback--error {
  background: #ffe8e8;
  color: #9f2d2d;
}
</style>
