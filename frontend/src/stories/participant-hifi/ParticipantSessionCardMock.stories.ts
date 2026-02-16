import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantSessionCardMock from './ParticipantSessionCardMock.vue';

const meta = {
  title: 'US-12-1 Participant HiFi/Components/SessionCard',
  component: ParticipantSessionCardMock,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantSessionCardMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileOpenSeat: Story = {
  args: {
    startTime: '10:00',
    track: 'Track A',
    title: 'Keynote: Product Vision',
    seatLabel: '空席あり',
    seatTone: 'open',
    reserved: false,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン' } },
  },
};

export const MobileReserved: Story = {
  args: {
    startTime: '11:30',
    track: 'Track B',
    title: 'API Design Clinic',
    seatLabel: '残りわずか',
    seatTone: 'few',
    reserved: true,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（予約済み表示）' } },
  },
};

export const TabletFullSeat: Story = {
  args: {
    startTime: '15:00',
    track: 'Track C',
    title: 'Scaling CQRS in Practice',
    seatLabel: '満席',
    seatTone: 'full',
    reserved: false,
    disabled: true,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（満席表示）' } },
  },
};
