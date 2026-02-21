import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminLoginCardMock from './AdminLoginCardMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminLoginCard',
  component: AdminLoginCardMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminLoginCardMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileCard: Story = {
  args: { disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（管理者ログイン入力）' } },
  },
};

export const LoadingCard: Story = {
  args: { disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（ログイン判定中）' } },
  },
};
