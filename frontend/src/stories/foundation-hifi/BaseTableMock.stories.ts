import type { Meta, StoryObj } from '@storybook/vue3-vite';
import BaseTableMock from './BaseTableMock.vue';

const meta = {
  title: 'US-13 Foundation HiFi/BaseTableMock',
  component: BaseTableMock,
  tags: ['autodocs'],
} satisfies Meta<typeof BaseTableMock>;

export default meta;
type Story = StoryObj<typeof meta>;

const rows = [
  { id: 'S-101', title: 'Design Tokens Overview', action: '編集' },
  { id: 'S-204', title: 'UX Feedback Ops', action: '確認' },
];

export const DefaultTable: Story = {
  args: { rows },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
  },
};
