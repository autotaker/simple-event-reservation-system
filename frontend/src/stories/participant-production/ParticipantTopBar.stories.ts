import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantTopBar from '../../components/participant/ParticipantTopBar.vue';

const meta = {
  title: 'US-12-1 Participant Production/Components/TopBar',
  component: ParticipantTopBar,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantTopBar>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    dateLabel: '2/16 (Mon) Day 1',
    guestName: 'Guest A12',
    disabled: false,
  },
};

export const Busy: Story = {
  args: {
    eventName: 'Tokyo Product Summit 2026',
    dateLabel: '2/16 (Mon) Day 1',
    guestName: 'Guest A12',
    disabled: true,
  },
};
