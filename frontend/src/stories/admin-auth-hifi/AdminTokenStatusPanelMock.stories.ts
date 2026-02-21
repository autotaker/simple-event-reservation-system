import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminTokenStatusPanelMock from './AdminTokenStatusPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminTokenStatusPanel',
  component: AdminTokenStatusPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminTokenStatusPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const ActiveTokenState: Story = {
  args: {
    ttlLabel: '09:45',
    lastRefreshAt: '2026-02-21 09:20',
    apiStatus: '利用可能',
    disabled: false,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（有効トークン運用）' } },
  },
};

export const ExpiredTokenState: Story = {
  args: {
    ttlLabel: '00:00',
    lastRefreshAt: '2026-02-21 08:55',
    apiStatus: '期限切れのため拒否',
    disabled: true,
  },
  parameters: {
    docs: {
      description: { story: '想定デバイス: 参加者スマートフォン（期限切れで再ログイン必要）' },
    },
  },
};
