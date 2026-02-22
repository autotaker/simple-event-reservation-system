import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminAuthLayoutHiFi from './AdminAuthLayoutHiFi.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/AdminAuthLayout',
  component: AdminAuthLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminAuthLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileUnauthenticated: Story = {
  args: { mode: 'unauthenticated' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（未認証の初期状態）' } },
    layout: 'centered',
  },
};

export const MobileAuthenticating: Story = {
  args: { mode: 'authenticating' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（認証中）' } },
    layout: 'centered',
  },
};

export const TabletAuthenticated: Story = {
  args: { mode: 'authenticated' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（認証済み）' } },
    layout: 'centered',
  },
};

export const MobileExpired401: Story = {
  args: { mode: 'expired' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（期限切れ401）' } },
    layout: 'centered',
  },
};

export const MobileRevoked401: Story = {
  args: { mode: 'revoked' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（失効401）' } },
    layout: 'centered',
  },
};
