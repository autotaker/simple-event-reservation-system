import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantFlowWireframeMock from './ParticipantFlowWireframeMock.vue';

const meta = {
  title: 'US-12-1 Participant/ParticipantFlowWireframeMock',
  component: ParticipantFlowWireframeMock,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantFlowWireframeMock>;

export default meta;

type Story = StoryObj<typeof meta>;

const baseArgs = {
  routeLabel: '/participant',
  title: '参加者導線（/participant）',
  summary: '参加者は予約操作を participant 画面で完結し、管理UIを意識せずに操作できる。',
  participantFeatures: ['セッション一覧表示', '予約一覧表示', 'マイページ表示', '予約 / 取消操作'],
  adminFeatures: ['管理一覧取得', 'セッション作成/編集', 'CSV出力', '運営チェックイン管理'],
  feedbackRules: [
    '予約完了後に成功メッセージを表示する',
    '取消完了後に予約一覧とマイページ表示を同期更新する',
    '失敗時は原因と再試行導線を同じ画面に表示する',
  ],
  handoffIssue: '#39',
} as const;

export const DefaultFlow: Story = {
  args: {
    ...baseArgs,
    actionLabel: '予約一覧を更新',
    status: 'default',
  },
};

export const LoadingState: Story = {
  args: {
    ...baseArgs,
    actionLabel: '更新中',
    status: 'loading',
  },
};

export const SuccessState: Story = {
  args: {
    ...baseArgs,
    actionLabel: '予約を確定',
    status: 'success',
  },
};

export const ErrorState: Story = {
  args: {
    ...baseArgs,
    actionLabel: '再試行',
    status: 'error',
  },
};
