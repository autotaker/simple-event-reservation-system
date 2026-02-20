import type { Meta, StoryObj } from '@storybook/vue3-vite';
import OperatorCheckInPanel from '../../components/operator/OperatorCheckInPanel.vue';
import OperatorReservationPanel from '../../components/operator/OperatorReservationPanel.vue';
import OperatorSessionTable from '../../components/operator/OperatorSessionTable.vue';
import { viewportParams } from './viewportPresets';

const meta = {
  title: 'US-13 CSS Foundation Production/OperatorFlowResponsive',
  tags: ['autodocs'],
  render: () => ({
    components: {
      OperatorSessionTable,
      OperatorReservationPanel,
      OperatorCheckInPanel,
    },
    template: `
      <main class="ui-shell ui-shell--operator">
        <h1 style="margin:0;">Event Reservation MVP</h1>
        <p class="ui-muted">運営導線での予約・チェックイン操作を確認します。</p>
        <p class="ui-status ui-status--loading">チェックイン履歴を更新中です...</p>

        <OperatorSessionTable
          :has-token="true"
          :sessions="[
            {
              sessionId: 'session-1',
              title: 'Session 1',
              startTime: '10:30',
              track: 'Track A',
              availabilityStatusLabel: '残りわずか',
              reserved: false,
              unavailable: false
            },
            {
              sessionId: 'session-2',
              title: 'Session 2',
              startTime: '13:30',
              track: 'Track B',
              availabilityStatusLabel: '満席',
              reserved: false,
              unavailable: true
            }
          ]"
        />

        <OperatorReservationPanel
          :has-token="true"
          :reservations="['keynote', 'session-1']"
          :registered="true"
          :registration-status-loaded="true"
        />

        <OperatorCheckInPanel
          :has-token="true"
          qr-code-payload="event-reservation://checkin?guestId=guest-100&reservations=keynote"
          selected-session-id="session-1"
          :session-options="[
            { sessionId: 'session-1', title: 'Session 1', startTime: '10:30' },
            { sessionId: 'session-2', title: 'Session 2', startTime: '13:30' }
          ]"
          :history="[
            {
              guestId: 'guest-100',
              checkInType: 'EVENT',
              checkInTypeLabel: 'イベント受付',
              sessionId: null,
              checkedInAtLabel: '2026/2/18 10:31:20'
            }
          ]"
          :history-loaded="true"
          result-message="guest-100 のイベント受付チェックインを記録しました。"
          result-tone="success"
        />
      </main>
    `,
  }),
} satisfies Meta;

export default meta;
type Story = StoryObj<typeof meta>;

export const Mobile390: Story = {
  parameters: viewportParams('mobile390'),
};

export const Tablet834: Story = {
  parameters: viewportParams('tablet834'),
};

export const Desktop1280: Story = {
  parameters: viewportParams('desktop1280'),
};
