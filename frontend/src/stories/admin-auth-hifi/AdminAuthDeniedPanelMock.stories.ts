import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminAuthDeniedPanelMock from './AdminAuthDeniedPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminAuthDeniedPanel',
  component: AdminAuthDeniedPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminAuthDeniedPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileDeniedState: Story = {
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン（期限切れ/失効済みトークンで拒否）',
      },
    },
  },
};
