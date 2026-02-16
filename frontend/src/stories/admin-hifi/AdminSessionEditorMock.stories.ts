import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminSessionEditorMock from './AdminSessionEditorMock.vue';

const meta = {
  title: 'US-12-2 Admin HiFi/Components/SessionEditor',
  component: AdminSessionEditorMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminSessionEditorMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileEditor: Story = {
  args: { disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(管理操作可)' } },
  },
};

export const TabletEditorLoading: Story = {
  args: { disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット(保存中)' } },
  },
};
