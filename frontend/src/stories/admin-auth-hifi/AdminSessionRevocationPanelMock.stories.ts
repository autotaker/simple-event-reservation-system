import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminSessionRevocationPanelMock from './AdminSessionRevocationPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminSessionRevocationPanel',
  component: AdminSessionRevocationPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminSessionRevocationPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileLogoutPanel: Story = {
  args: { disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（当該トークン失効）' } },
  },
};

export const LogoutInProgressPanel: Story = {
  args: { disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（失効処理中）' } },
  },
};
