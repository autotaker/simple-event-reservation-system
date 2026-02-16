import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminRouteGuardMock from './AdminRouteGuardMock.vue';

const meta = {
  title: 'US-12-2 Flow/AdminRouteGuardMock',
  component: AdminRouteGuardMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminRouteGuardMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const AdminAllowed: Story = {
  args: {
    route: '/admin',
    title: '管理導線（許可）',
    description: '管理者権限ユーザーは /admin で管理機能のみ操作できる。',
    visibleItems: ['管理一覧', 'セッション作成/編集', 'CSV 出力'],
    hiddenItems: ['参加者向け予約操作', 'マイページ導線'],
    actionLabel: '管理操作へ進む',
    state: 'success',
  },
};

export const AccessChecking: Story = {
  args: {
    route: '/admin',
    title: 'アクセス判定中（loading）',
    description: '権限判定が完了するまで、主要操作は無効化して誤操作を防止する。',
    visibleItems: ['判定中フィードバック', 'ヘッダーの読み込み状態'],
    hiddenItems: ['管理操作ボタンの有効化', '権限確定前のコンテンツ表示確定'],
    actionLabel: '権限を確認中',
    state: 'loading',
  },
};

export const ForbiddenDirectAccess: Story = {
  args: {
    route: '/admin (forbidden)',
    title: '権限外アクセス拒否（error）',
    description: 'participant 権限の /admin 直接アクセスは 403 で拒否し、復帰導線を提示する。',
    visibleItems: ['403 エラーメッセージ', '拒否理由', '/participant へ戻るボタン'],
    hiddenItems: ['管理画面本文', '管理操作ボタン'],
    actionLabel: 'participant に戻る',
    state: 'error',
  },
};
