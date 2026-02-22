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
  args: { mode: 'default' },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン。タイトル主表示 + 時刻/トラック副表示。',
      },
    },
  },
};

export const Loading: Story = {
  args: { mode: 'loading' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン。更新中状態。' } },
  },
};

export const Success: Story = {
  args: { mode: 'success' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット。更新成功状態。' } },
  },
};

export const Error: Story = {
  args: { mode: 'error' },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認。取得失敗状態。' } },
  },
};

export const Fallback: Story = {
  args: { mode: 'fallback' },
  parameters: {
    docs: {
      description: {
        story: '表示データ不足時。`sessionId` 生表示ではなくフォールバック文言を表示。',
      },
    },
  },
};
