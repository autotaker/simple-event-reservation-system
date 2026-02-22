import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminTokenStatusPanelMock from './AdminTokenStatusPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminTokenStatusPanel',
  component: AdminTokenStatusPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminTokenStatusPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const AuthenticatedState: Story = {
  args: {
    operatorId: 'ops_admin_01',
    expiresAt: '2026-02-22T10:00:00+09:00',
    apiStatus: '利用可能',
    disabled: false,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（認証済み）' } },
  },
};

export const PreAuthState: Story = {
  args: {
    operatorId: 'ops_admin_01',
    expiresAt: '2026-02-22T09:30:00+09:00',
    apiStatus: '認証前',
    disabled: true,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（認証判定前）' } },
  },
};
