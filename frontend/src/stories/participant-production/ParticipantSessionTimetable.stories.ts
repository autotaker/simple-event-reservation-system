import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantSessionTimetable from '../../components/participant/ParticipantSessionTimetable.vue';

const baseSessions = [
  {
    sessionId: 'keynote',
    title: 'Opening Keynote',
    startTime: '09:00',
    track: 'Keynote',
    availabilityStatus: 'OPEN',
  },
  {
    sessionId: 'session-1',
    title: 'Session 1',
    startTime: '10:30',
    track: 'Track A',
    availabilityStatus: 'OPEN',
  },
  {
    sessionId: 'session-2',
    title: 'Session 2',
    startTime: '10:30',
    track: 'Track B',
    availabilityStatus: 'FEW_LEFT',
  },
  {
    sessionId: 'session-3',
    title: 'Session 3',
    startTime: '10:30',
    track: 'Track C',
    availabilityStatus: 'FULL',
  },
];

const meta = {
  title: 'US-14 Participant Production/Components/SessionTimetable',
  component: ParticipantSessionTimetable,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantSessionTimetable>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    sessions: baseSessions,
    reservedSessionIds: [],
    disabled: false,
  },
};

export const WithReserved: Story = {
  args: {
    sessions: baseSessions,
    reservedSessionIds: ['session-1'],
    disabled: false,
  },
};

export const Loading: Story = {
  args: {
    sessions: baseSessions,
    reservedSessionIds: [],
    disabled: true,
  },
};
