import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantReservationPanelMock from './ParticipantReservationPanelMock.vue';

const meta = {
  title: 'US-12-1 Participant HiFi/Components/ReservationPanel',
  component: ParticipantReservationPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantReservationPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

const reservations = [
  { id: 'r1', title: 'Keynote: Product Vision', time: '10:00 | Track A' },
  { id: 'r2', title: 'API Design Clinic', time: '11:30 | Track B' },
];

export const MobileReservationPanel: Story = {
  args: { reservations, disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン' } },
  },
};

export const TabletReservationPanelLoading: Story = {
  args: { reservations, disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット（更新中）' } },
  },
};
