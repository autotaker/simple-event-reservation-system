import type { Meta, StoryObj } from '@storybook/vue3-vite';
import Issue35MyPageReadablePanelMock from './Issue35MyPageReadablePanelMock.vue';

const meta = {
  title: 'Issue-35 HiFi/Components/MyPageReadablePanelMock',
  component: Issue35MyPageReadablePanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof Issue35MyPageReadablePanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { mode: 'default', variant: 'normal' },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン。タイトル主表示 + 時刻/トラック副表示。',
      },
    },
  },
};

export const LoadingKeepList: Story = {
  args: { mode: 'loading', variant: 'normal' },
  parameters: {
    docs: {
      description: {
        story: '更新中でも直前の予約リストを維持して表示する。',
      },
    },
  },
};

export const ErrorKeepList: Story = {
  args: { mode: 'error', variant: 'normal' },
  parameters: {
    docs: {
      description: {
        story: '取得失敗でも直前の予約リストを維持し、エラー文言を併記する。',
      },
    },
  },
};

export const Success: Story = {
  args: { mode: 'success', variant: 'normal' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット。更新成功状態。' } },
  },
};

export const MixedFallbackRow: Story = {
  args: { mode: 'default', variant: 'mixed-fallback' },
  parameters: {
    docs: {
      description: {
        story: '欠損行のみフォールバックを適用する（行単位）。正常データ行は可読表示を維持する。',
      },
    },
  },
};

export const AllFallback: Story = {
  args: { mode: 'default', variant: 'all-fallback' },
  parameters: {
    docs: {
      description: {
        story: '全件欠損時のフォールバック表示。`sessionId` 生表示は行わない。',
      },
    },
  },
};

export const Empty: Story = {
  args: { mode: 'default', variant: 'empty' },
  parameters: {
    docs: {
      description: {
        story: '予約0件時は `予約はありません。` を表示する。',
      },
    },
  },
};

export const ManyReservations: Story = {
  args: { mode: 'default', variant: 'many' },
  parameters: {
    docs: {
      description: {
        story: '多件時はリスト領域のみ縦スクロールし、QR導線を維持する。',
      },
    },
  },
};
