import type { Meta, StoryObj } from '@storybook/vue3-vite';
import FlowSeparationWireframeMock from './FlowSeparationWireframeMock.vue';

const meta = {
  title: 'US-12 Flow/FlowSeparationWireframeMock',
  component: FlowSeparationWireframeMock,
  tags: ['autodocs'],
} satisfies Meta<typeof FlowSeparationWireframeMock>;

export default meta;

type Story = StoryObj<typeof meta>;

export const ParticipantFlow: Story = {
  args: {
    routeLabel: '/participant',
    title: '参加者導線',
    summary: '参加者は予約操作を participant 画面で完結し、管理操作へ迷い込まない。',
    allowedFeatures: ['セッション一覧表示', '予約一覧表示', 'マイページ表示'],
    blockedFeatures: ['管理一覧取得', 'セッション作成/編集', 'CSV出力'],
    handoffIssue: '#39',
    actionLabel: 'participant へ進む',
    tone: 'participant',
    status: 'success',
  },
};

export const AdminFlow: Story = {
  args: {
    routeLabel: '/admin',
    title: '運営導線',
    summary: '運営は admin 画面で管理操作を完結し、参加者向け機能と責務を分離する。',
    allowedFeatures: ['管理一覧表示', 'セッション作成/編集', 'CSV出力'],
    blockedFeatures: ['予約一覧表示', 'マイページ表示', '参加者予約操作'],
    handoffIssue: '#40',
    actionLabel: 'admin へ進む',
    tone: 'admin',
    status: 'default',
  },
};

export const ForbiddenFlow: Story = {
  args: {
    routeLabel: '/admin (forbidden)',
    title: '権限外アクセス拒否導線',
    summary:
      'participant 権限ユーザーの /admin 直接アクセス時は拒否理由を明示し、participant へ戻す。',
    allowedFeatures: ['拒否理由表示', 'participant へ戻る導線'],
    blockedFeatures: ['管理画面コンテンツ表示', '管理操作ボタンの活性化'],
    handoffIssue: '#40',
    actionLabel: 'participant に戻る',
    tone: 'forbidden',
    status: 'error',
  },
};

export const LoadingGateCheck: Story = {
  args: {
    routeLabel: '/admin (forbidden)',
    title: 'アクセス判定中',
    summary: '権限判定中は導線を確定せず、操作ボタンを無効化して誤操作を防ぐ。',
    allowedFeatures: ['判定中フィードバック'],
    blockedFeatures: ['判定完了前の遷移確定'],
    handoffIssue: '#40',
    actionLabel: '判定中',
    tone: 'forbidden',
    status: 'loading',
  },
};
