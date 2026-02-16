import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminRouteGuardMock from './AdminRouteGuardMock.vue';

const meta = {
  title: 'US-12 Admin/AdminRouteGuardMock',
  component: AdminRouteGuardMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminRouteGuardMock>;

export default meta;

type Story = StoryObj<typeof meta>;

export const AdminAuthorized: Story = {
  args: {
    route: '/admin',
    title: '管理導線（許可）',
    summary: '運営トークン検証後に管理機能のみ表示し、参加者導線への機能混在を防ぐ。',
    visibleBlocks: ['管理一覧テーブル', 'セッション作成/編集フォーム', 'CSV出力アクション'],
    hiddenBlocks: ['参加者セッション予約UI', '予約一覧UI', 'マイページQR表示'],
    primaryAction: '管理機能を操作する',
    feedback: '管理権限を確認できたため admin 操作を有効化しています。',
    state: 'success',
    stateTone: 'admin',
  },
};

export const AdminPermissionChecking: Story = {
  args: {
    route: '/admin',
    title: '管理導線（判定中）',
    summary: '画面遷移直後は権限判定を完了するまで管理操作を無効化する。',
    visibleBlocks: ['判定中メッセージ', 'スケルトン枠（管理UI本体は未表示）'],
    hiddenBlocks: ['管理一覧テーブル', '作成/編集フォーム', 'CSV出力アクション'],
    primaryAction: '権限判定中',
    feedback: '権限を確認中です。判定完了まで操作できません。',
    state: 'loading',
    stateTone: 'admin',
  },
};

export const AdminForbidden: Story = {
  args: {
    route: '/admin (forbidden)',
    title: '権限外アクセス拒否',
    summary:
      'participant 権限から /admin へ直接アクセスした場合は拒否理由を示し、復帰導線のみ提供する。',
    visibleBlocks: ['403 Forbidden 理由文', 'participant へ戻るボタン'],
    hiddenBlocks: ['管理一覧テーブル', 'セッション作成/編集フォーム', 'CSV出力アクション'],
    primaryAction: 'participant に戻る',
    feedback: 'このアカウントでは管理画面にアクセスできません。参加者画面へ戻ってください。',
    state: 'error',
    stateTone: 'forbidden',
  },
};
