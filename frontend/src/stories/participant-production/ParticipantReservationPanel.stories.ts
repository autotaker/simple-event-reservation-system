import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantReservationPanel from '../../components/participant/ParticipantReservationPanel.vue';

const meta = {
  title: 'US-12-1 Participant Production/Components/ReservationPanel',
  component: ParticipantReservationPanel,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantReservationPanel>;

export default meta;
type Story = StoryObj<typeof meta>;

const reservations = [
  { id: 'keynote', title: 'Keynote: Product Vision', time: '10:00 | Track A' },
  { id: 'session-a1', title: 'API Design Clinic', time: '11:30 | Track B' },
];

export const LoggedIn: Story = {
  args: {
    reservations,
    hasToken: true,
    registered: true,
    registrationStatusLoaded: true,
    disabled: false,
  },
};

export const Loading: Story = {
  args: {
    reservations,
    hasToken: true,
    registered: false,
    registrationStatusLoaded: true,
    disabled: true,
  },
};

export const EmptyState: Story = {
  args: {
    reservations: [],
    hasToken: true,
    registered: false,
    registrationStatusLoaded: true,
    disabled: false,
  },
};
