import type { Meta, StoryObj } from '@storybook/vue3-vite';
import OperatorCheckInPanel from '../../components/operator/OperatorCheckInPanel.vue';

const meta = {
  title: 'US-11 Operator Production/Components/CheckInPanel',
  component: OperatorCheckInPanel,
  tags: ['autodocs'],
} satisfies Meta<typeof OperatorCheckInPanel>;

export default meta;
type Story = StoryObj<typeof meta>;

const sessions = [
  { sessionId: 'session-1', title: 'Session 1', startTime: '10:30' },
  { sessionId: 'session-2', title: 'Session 2', startTime: '10:30' },
];

const history = [
  {
    guestId: 'guest-100',
    checkInType: 'EVENT',
    checkInTypeLabel: 'イベント受付',
    sessionId: null,
    checkedInAtLabel: '2026/2/18 10:31:20',
  },
];

export const Default: Story = {
  args: {
    hasToken: true,
    qrCodePayload: 'event-reservation://checkin?guestId=guest-100&reservations=keynote,session-1',
    selectedSessionId: 'session-1',
    sessionOptions: sessions,
    history: [],
    historyLoaded: false,
    resultMessage: '',
    resultTone: '',
  },
};

export const Loading: Story = {
  args: {
    ...Default.args,
    disabled: true,
    resultMessage: 'チェックイン履歴を更新中です...',
    resultTone: 'success',
  },
};

export const Success: Story = {
  args: {
    ...Default.args,
    history,
    historyLoaded: true,
    resultMessage: 'guest-100 のイベント受付チェックインを記録しました。',
    resultTone: 'success',
  },
};

export const Error: Story = {
  args: {
    ...Default.args,
    history,
    historyLoaded: true,
    resultMessage: 'チェックイン書き込み権限がありません。',
    resultTone: 'error',
  },
};
