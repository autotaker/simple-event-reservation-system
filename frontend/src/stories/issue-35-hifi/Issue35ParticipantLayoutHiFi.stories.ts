import type { Meta, StoryObj } from '@storybook/vue3-vite';
import Issue35ParticipantLayoutHiFi from './Issue35ParticipantLayoutHiFi.vue';

const meta = {
  title: 'Issue-35 HiFi/Layout/ParticipantReadableReservation',
  component: Issue35ParticipantLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof Issue35ParticipantLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { mode: 'default', contentVariant: 'normal' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)。通常表示。' } },
    layout: 'centered',
  },
};

export const LoadingKeepList: Story = {
  args: { mode: 'loading', contentVariant: 'normal' },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン。更新中でも予約リストを維持。',
      },
    },
    layout: 'centered',
  },
};

export const ErrorKeepList: Story = {
  args: { mode: 'error', contentVariant: 'normal' },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 来場前PC確認。取得失敗でも予約リストを維持。',
      },
    },
    layout: 'fullscreen',
  },
};

export const Success: Story = {
  args: { mode: 'success', contentVariant: 'normal' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)。更新成功状態。' } },
    layout: 'centered',
  },
};

export const MixedFallbackText: Story = {
  args: { mode: 'default', contentVariant: 'mixed-fallback' },
  parameters: {
    docs: {
      description: {
        story: '欠損行のみ `不明なセッション` を表示する（行単位フォールバック）。',
      },
    },
    layout: 'centered',
  },
};

export const EmptyReservations: Story = {
  args: { mode: 'default', contentVariant: 'empty' },
  parameters: {
    docs: {
      description: {
        story: '予約0件時の表示。',
      },
    },
    layout: 'centered',
  },
};

export const ManyReservations: Story = {
  args: { mode: 'default', contentVariant: 'many' },
  parameters: {
    docs: {
      description: {
        story: '多件時はリスト領域のみスクロールさせ、QR導線を維持する。',
      },
    },
    layout: 'centered',
  },
};
