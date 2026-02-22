import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminForbiddenPanelMock from './AdminForbiddenPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminForbiddenPanel',
  component: AdminForbiddenPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminForbiddenPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileForbidden403: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（権限不足403）' } },
  },
};
