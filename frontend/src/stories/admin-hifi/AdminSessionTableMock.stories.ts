import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminSessionTableMock from './AdminSessionTableMock.vue';

const meta = {
  title: 'US-12-2 Admin HiFi/Components/SessionTable',
  component: AdminSessionTableMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminSessionTableMock>;

export default meta;
type Story = StoryObj<typeof meta>;

const sessions = [
  { id: 'a1', slot: '10:00', track: 'Track A', title: 'Keynote: Product Vision' },
  { id: 'a2', slot: '11:30', track: 'Track B', title: 'API Design Clinic' },
  { id: 'a3', slot: '14:00', track: 'Track C', title: 'Secure Check-in Operations' },
];

export const MobileTable: Story = {
  args: { sessions, disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(編集操作の入口確認)' } },
  },
};

export const TabletTableLoading: Story = {
  args: { sessions, disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット(データ更新待機)' } },
  },
};
