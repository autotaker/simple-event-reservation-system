import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminSessionRevocationPanelMock from './AdminSessionRevocationPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminSessionRevocationPanel',
  component: AdminSessionRevocationPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminSessionRevocationPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileRevocationPanel: Story = {
  args: { disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（失効運用操作）' } },
  },
};

export const DisabledRevocationPanel: Story = {
  args: { disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（失効実行中）' } },
  },
};
