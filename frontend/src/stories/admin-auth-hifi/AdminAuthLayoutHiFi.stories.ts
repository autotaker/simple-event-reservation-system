import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminAuthLayoutHiFi from './AdminAuthLayoutHiFi.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/AdminAuthLayout',
  component: AdminAuthLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminAuthLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileAuthDefault: Story = {
  args: { mode: 'default' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（ログイン前の初期状態）' } },
    layout: 'centered',
  },
};

export const MobileAuthLoading: Story = {
  args: { mode: 'loading' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（認証確認中）' } },
    layout: 'centered',
  },
};

export const TabletAuthSuccess: Story = {
  args: { mode: 'success' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（更新成功後）' } },
    layout: 'centered',
  },
};

export const MobileAuthError: Story = {
  args: { mode: 'error' },
  parameters: {
    docs: {
      description: { story: '想定デバイス: 参加者スマートフォン（期限切れ/失効トークンで拒否）' },
    },
    layout: 'centered',
  },
};
