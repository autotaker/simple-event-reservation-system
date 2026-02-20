import type { Meta, StoryObj } from '@storybook/vue3-vite';
import StatusMessageMock from './StatusMessageMock.vue';

const meta = {
  title: 'US-13 Foundation HiFi/StatusMessageMock',
  component: StatusMessageMock,
  tags: ['autodocs'],
} satisfies Meta<typeof StatusMessageMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Loading: Story = {
  args: { tone: 'loading', message: '更新中です。しばらくお待ちください。' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
  },
};

export const Success: Story = {
  args: { tone: 'success', message: '保存が完了しました。' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
  },
};

export const Error: Story = {
  args: { tone: 'error', message: '保存に失敗しました。入力内容を確認してください。' },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)' } },
  },
};
