import type { Meta, StoryObj } from '@storybook/vue3-vite';
import Issue35ReservationPanelMock from './Issue35ReservationPanelMock.vue';

const meta = {
  title: 'Issue-35 HiFi/Components/ReservationPanelMock',
  component: Issue35ReservationPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof Issue35ReservationPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const WithCancelCta: Story = {
  parameters: {
    docs: {
      description: { story: '想定デバイス: 参加者スマートフォン。キャンセル導線維持を固定する。' },
    },
  },
};
