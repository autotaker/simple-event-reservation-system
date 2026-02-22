import type { Meta, StoryObj } from '@storybook/vue3-vite';
import Issue35TopBarMock from './Issue35TopBarMock.vue';

const meta = {
  title: 'Issue-35 HiFi/Components/TopBarMock',
  component: Issue35TopBarMock,
  tags: ['autodocs'],
} satisfies Meta<typeof Issue35TopBarMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Mobile: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
  },
};

export const Tablet: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
  },
};
