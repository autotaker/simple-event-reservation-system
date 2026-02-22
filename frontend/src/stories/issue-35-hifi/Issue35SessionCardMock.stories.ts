import type { Meta, StoryObj } from '@storybook/vue3-vite';
import Issue35SessionCardMock from './Issue35SessionCardMock.vue';

const meta = {
  title: 'Issue-35 HiFi/Components/SessionCardMock',
  component: Issue35SessionCardMock,
  tags: ['autodocs'],
} satisfies Meta<typeof Issue35SessionCardMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン。主表示はタイトル、補助表示は時刻・トラック。',
      },
    },
  },
};
