import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantSessionCard from '../../components/participant/ParticipantSessionCard.vue';

const meta = {
  title: 'US-12-1 Participant Production/Components/SessionCard',
  component: ParticipantSessionCard,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantSessionCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const OpenSeat: Story = {
  args: {
    startTime: '10:00',
    track: 'Track A',
    title: 'Keynote: Product Vision',
    seatLabel: '受付中',
    seatTone: 'open',
    reserved: false,
    disabled: false,
  },
};

export const ReservedFewLeft: Story = {
  args: {
    startTime: '11:30',
    track: 'Track B',
    title: 'API Design Clinic',
    seatLabel: '残りわずか',
    seatTone: 'few',
    reserved: true,
    disabled: true,
  },
};

export const FullSeat: Story = {
  args: {
    startTime: '15:00',
    track: 'Track C',
    title: 'Scaling CQRS in Practice',
    seatLabel: '満席',
    seatTone: 'full',
    reserved: false,
    disabled: true,
  },
};
