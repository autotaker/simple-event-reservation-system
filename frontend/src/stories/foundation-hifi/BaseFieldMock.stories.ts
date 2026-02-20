import type { Meta, StoryObj } from '@storybook/vue3-vite';
import BaseFieldMock from './BaseFieldMock.vue';

const meta = {
  title: 'US-13 Foundation HiFi/BaseFieldMock',
  component: BaseFieldMock,
  tags: ['autodocs'],
} satisfies Meta<typeof BaseFieldMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultField: Story = {
  args: { label: '参加者ID', placeholder: 'guest-001', state: 'default' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
  },
};

export const ErrorField: Story = {
  args: { label: '参加者ID', placeholder: 'guest-001', state: 'error' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
  },
};

export const DisabledField: Story = {
  args: { label: '参加者ID', placeholder: 'guest-001', disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)' } },
  },
};
