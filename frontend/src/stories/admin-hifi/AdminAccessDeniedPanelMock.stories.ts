import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminAccessDeniedPanelMock from './AdminAccessDeniedPanelMock.vue';

const meta = {
  title: 'US-12-2 Admin HiFi/Components/AccessDeniedPanel',
  component: AdminAccessDeniedPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminAccessDeniedPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileDeniedState: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(権限なし/adminアクセス拒否)' } },
  },
};

export const TabletDeniedState: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット(権限なし時のフィードバック)' } },
  },
};
