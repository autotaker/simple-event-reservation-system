import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminForbiddenLayoutHiFi from './AdminForbiddenLayoutHiFi.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/AdminForbiddenLayout',
  component: AdminForbiddenLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminForbiddenLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileForbidden403: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（/admin で権限不足403）' } },
    layout: 'centered',
  },
};

export const TabletForbidden403: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（/admin で権限不足403）' } },
    layout: 'centered',
  },
};
