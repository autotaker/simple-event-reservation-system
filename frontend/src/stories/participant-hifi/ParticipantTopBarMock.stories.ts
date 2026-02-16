import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantTopBarMock from './ParticipantTopBarMock.vue';

const meta = {
  title: 'US-12-1 Participant HiFi/Components/TopBar',
  component: ParticipantTopBarMock,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantTopBarMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileHeader: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    dateLabel: '2/16 (Mon) Day 1',
    guestName: 'Guest A12',
    showMenu: true,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン' } },
  },
};

export const TabletHeader: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    dateLabel: '2/16 (Mon) Day 1',
    guestName: 'Guest A12',
    showMenu: false,
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット' } },
  },
};
