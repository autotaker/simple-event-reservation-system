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
  title: '参加者ポータル',
  summary:
    '予約操作を1画面で完結するため、セッション選択・予約確認・マイページ確認を同時に見渡せる構成にする。',
  participantFeatures: ['セッション一覧', '予約一覧', 'マイページ', '予約/取消'],
  adminFeatures: ['管理一覧', 'セッション作成/編集', 'CSV出力', '運営チェックイン'],
  sessionCards: [
    {
      id: 's1',
      time: '10:00',
      track: 'Track A',
      title: 'Keynote: Product Vision',
      cta: '予約する',
    },
    {
      id: 's2',
      time: '11:30',
      track: 'Track B',
      title: 'API Design Clinic',
      cta: '予約済み',
      reserved: true,
    },
  ],
  reservations: [
    { id: 'r1', title: 'Keynote: Product Vision', state: '確定' },
    { id: 'r2', title: 'API Design Clinic', state: '取消待ちなし' },
  ],
  myPageNote: '受付QRと予約サマリーを同一パネルで確認。会場移動中でも状態確認を迷わせない。',
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
