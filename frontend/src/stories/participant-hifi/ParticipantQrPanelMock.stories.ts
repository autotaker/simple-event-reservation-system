import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantQrPanelMock from './ParticipantQrPanelMock.vue';

const meta = {
  title: 'US-12-1 Participant HiFi/Components/MyPageQrPanel',
  component: ParticipantQrPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantQrPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileQrPanel: Story = {
  args: { caption: '受付QRコード' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン' } },
  },
};

export const TabletQrPanel: Story = {
  args: { caption: '受付QRコード（受付補助表示）' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット' } },
  },
};
