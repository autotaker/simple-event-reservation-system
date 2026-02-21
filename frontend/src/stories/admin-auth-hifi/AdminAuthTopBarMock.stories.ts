import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminAuthTopBarMock from './AdminAuthTopBarMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminAuthTopBar',
  component: AdminAuthTopBarMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminAuthTopBarMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileHeader: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    sectionLabel: '管理者認証ポータル /admin/auth',
    adminName: 'Ops Admin',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（再認証時ヘッダー）' } },
  },
};

export const TabletHeader: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    sectionLabel: '管理者認証ポータル /admin/auth',
    adminName: 'Ops Shift Lead',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット' } },
  },
};
