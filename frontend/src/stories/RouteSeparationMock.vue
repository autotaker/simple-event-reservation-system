<template>
  <article class="route-separation-mock" :data-state="state">
    <header>
      <p class="eyebrow">Issue #30 Mock</p>
      <h3>{{ title }}</h3>
      <p>{{ description }}</p>
    </header>

    <section class="flow-grid">
      <div class="panel">
        <h4>想定URL</h4>
        <code>{{ routePath }}</code>
      </div>
      <div class="panel">
        <h4>表示モジュール</h4>
        <ul>
          <li v-for="module in visibleModules" :key="module">{{ module }}</li>
        </ul>
      </div>
      <div class="panel">
        <h4>主操作</h4>
        <button type="button" :disabled="state === 'loading' || state === 'forbidden'">
          {{ ctaLabel }}
        </button>
      </div>
    </section>

    <p v-if="state === 'forbidden'" class="feedback error">
      管理画面へのアクセス権がありません。参加者トップへ戻ります。
    </p>
    <p v-else-if="state === 'success'" class="feedback success">
      参加者導線と管理導線の分離が完了し、不要なUIは非表示です。
    </p>
    <p v-else-if="state === 'loading'" class="feedback loading">
      ルート遷移を検証しています...
    </p>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(
  defineProps<{
    persona: 'participant' | 'admin' | 'forbidden';
    state?: 'default' | 'loading' | 'success' | 'forbidden';
  }>(),
  {
    state: 'default',
  },
);

const title = computed((): string => {
  if (props.persona === 'participant') {
    return '参加者ポータル';
  }
  if (props.persona === 'admin') {
    return '管理ポータル';
  }
  return 'アクセス制御';
});

const description = computed((): string => {
  if (props.persona === 'participant') {
    return '予約・マイページのみを表示し、管理機能は表示しません。';
  }
  if (props.persona === 'admin') {
    return 'セッション管理とCSV出力のみを表示し、参加者導線は分離します。';
  }
  return '参加者が /admin へ直接アクセスしたときの拒否状態です。';
});

const routePath = computed((): string => {
  if (props.persona === 'participant') {
    return '/participant';
  }
  if (props.persona === 'admin') {
    return '/admin';
  }
  return '/admin (tokenなし)';
});

const visibleModules = computed((): string[] => {
  if (props.persona === 'participant') {
    return ['セッション一覧', '予約一覧', 'マイページ'];
  }
  if (props.persona === 'admin') {
    return ['管理一覧', 'セッション作成/編集', 'CSV出力'];
  }
  return ['アクセス拒否メッセージ', '参加者トップへのリダイレクト'];
});

const ctaLabel = computed((): string => {
  if (props.state === 'loading') {
    return '遷移中...';
  }
  if (props.persona === 'participant') {
    return '予約画面へ進む';
  }
  if (props.persona === 'admin') {
    return '管理ダッシュボードを開く';
  }
  return '参加者トップへ戻る';
});
</script>

<style scoped>
.route-separation-mock {
  display: grid;
  gap: 14px;
  max-width: 680px;
  padding: 20px;
  border: 1px solid #d0d7de;
  border-radius: 10px;
  background: #ffffff;
}

.eyebrow {
  margin: 0;
  font-size: 12px;
  letter-spacing: 0.04em;
  color: #57606a;
  text-transform: uppercase;
}

.route-separation-mock h3 {
  margin: 0;
}

.route-separation-mock p {
  margin: 0;
}

.flow-grid {
  display: grid;
  gap: 10px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.panel {
  display: grid;
  gap: 8px;
  padding: 12px;
  border: 1px solid #d8dee4;
  border-radius: 8px;
  background: #f6f8fa;
}

.panel h4 {
  margin: 0;
  font-size: 14px;
}

.panel ul {
  margin: 0;
  padding-left: 18px;
}

.panel button {
  width: fit-content;
  padding: 8px 12px;
  border: 1px solid #1f2328;
  border-radius: 6px;
  background: #ffffff;
}

.panel code {
  font-size: 13px;
}

.feedback {
  padding: 10px;
  border-radius: 8px;
}

.feedback.success {
  color: #1a7f37;
  background: #dafbe1;
}

.feedback.error {
  color: #d1242f;
  background: #ffebe9;
}

.feedback.loading {
  color: #0969da;
  background: #ddf4ff;
}

.route-separation-mock[data-state='forbidden'] {
  border-color: #d1242f;
}

.route-separation-mock[data-state='success'] {
  border-color: #1a7f37;
}
</style>
