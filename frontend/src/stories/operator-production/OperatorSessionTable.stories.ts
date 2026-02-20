import type { Meta, StoryObj } from '@storybook/vue3-vite';
import OperatorSessionTable from '../../components/operator/OperatorSessionTable.vue';

const meta = {
  title: 'US-11 Operator Production/Components/SessionTable',
  component: OperatorSessionTable,
  tags: ['autodocs'],
} satisfies Meta<typeof OperatorSessionTable>;

export default meta;
type Story = StoryObj<typeof meta>;

const sessions = [
  {
    sessionId: 'session-1',
    title: 'Session 1',
    startTime: '10:30',
    track: 'Track A',
    availabilityStatusLabel: '残りわずか',
    reserved: false,
    unavailable: false,
  },
  {
    sessionId: 'session-2',
    title: 'Session 2',
    startTime: '10:30',
    track: 'Track B',
    availabilityStatusLabel: '満席',
    reserved: false,
    unavailable: true,
  },
];

export const Default: Story = {
  args: {
    hasToken: true,
    sessions,
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
    hasToken: true,
    sessions: [
      {
        ...sessions[0],
        reserved: true,
      },
    ],
  },
};

export const Error: Story = {
  args: {
    hasToken: false,
    sessions: [],
  },
};
