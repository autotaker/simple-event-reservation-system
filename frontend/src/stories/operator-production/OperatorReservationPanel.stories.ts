import type { Meta, StoryObj } from '@storybook/vue3-vite';
import OperatorReservationPanel from '../../components/operator/OperatorReservationPanel.vue';

const meta = {
  title: 'US-11 Operator Production/Components/ReservationPanel',
  component: OperatorReservationPanel,
  tags: ['autodocs'],
} satisfies Meta<typeof OperatorReservationPanel>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    hasToken: true,
    reservations: ['keynote', 'session-1'],
    registered: true,
    registrationStatusLoaded: true,
  },
};

export const Loading: Story = {
  args: {
    ...Default.args,
    disabled: true,
  },
};

export const Success: Story = {
  args: {
    ...Default.args,
    reservations: ['keynote', 'session-1', 'session-7'],
    registered: true,
    registrationStatusLoaded: true,
  },
};

export const Error: Story = {
  args: {
    hasToken: false,
    reservations: [],
    registered: false,
    registrationStatusLoaded: false,
  },
};
