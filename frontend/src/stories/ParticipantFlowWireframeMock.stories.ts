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
  title: '参加者ポータル',
  subtitle: 'ようこそ、Guest A12',
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
  qrCaption: '受付QRコード',
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
