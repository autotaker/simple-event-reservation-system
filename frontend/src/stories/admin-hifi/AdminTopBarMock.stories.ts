import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminTopBarMock from './AdminTopBarMock.vue';

const meta = {
  title: 'US-12-2 Admin HiFi/Components/AdminTopBar',
  component: AdminTopBarMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminTopBarMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileHeader: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    sectionLabel: '運営管理ポータル /admin',
    adminName: 'Ops Admin',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(誤遷移時にも表示されるヘッダー)' } },
  },
};

export const TabletHeader: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    sectionLabel: '運営管理ポータル /admin',
    adminName: 'Ops Shift Lead',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット' } },
  },
};
