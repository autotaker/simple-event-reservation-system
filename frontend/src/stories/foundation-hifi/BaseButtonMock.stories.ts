import type { Meta, StoryObj } from '@storybook/vue3-vite';
import BaseButtonMock from './BaseButtonMock.vue';

const meta = {
  title: 'US-13 Foundation HiFi/BaseButtonMock',
  component: BaseButtonMock,
  tags: ['autodocs'],
} satisfies Meta<typeof BaseButtonMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  args: { label: 'Primary action', tone: 'primary' },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)' } },
  },
};

export const Secondary: Story = {
  args: { label: 'Secondary action', tone: 'secondary' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
  },
};

export const Disabled: Story = {
  args: { label: 'Disabled action', tone: 'primary', disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
  },
};
