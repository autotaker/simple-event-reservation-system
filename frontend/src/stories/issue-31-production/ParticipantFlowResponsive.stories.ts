import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantQrPanel from '../../components/participant/ParticipantQrPanel.vue';
import ParticipantReservationPanel from '../../components/participant/ParticipantReservationPanel.vue';
import ParticipantSessionCard from '../../components/participant/ParticipantSessionCard.vue';
import ParticipantTopBar from '../../components/participant/ParticipantTopBar.vue';
import { fixedFrameDecorator } from './fixedFrameDecorator';

const meta = {
  title: 'US-13 CSS Foundation Production/ParticipantFlowResponsive',
  tags: ['autodocs'],
  render: () => ({
    components: {
      ParticipantTopBar,
      ParticipantSessionCard,
      ParticipantReservationPanel,
      ParticipantQrPanel,
    },
    data() {
      return {
        qrImageUrl:
          'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAArElEQVR4nO3QQQ0AIBDAsAP/nkEEj4ZkVbCtmTMzP3sD6N4AwgAiAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAkgIICGAhAASAsgB3Q4BfM3v5fAAAAABJRU5ErkJggg==',
      };
    },
    template: `
      <main class="ui-shell ui-shell--participant">
        <ParticipantTopBar
          event-name="Tokyo Product Summit 2026"
          date-label="2/16 (Mon) Day 1"
          guest-name="Guest A12"
        />

        <p class="ui-status ui-status--success">予約情報を取得しました。</p>

        <section class="ui-layout-split">
          <section class="ui-layout-stack">
            <header style="display:flex;align-items:center;justify-content:space-between;gap:8px;">
              <h2 style="margin:0;font-size:14px;">セッション一覧</h2>
              <button class="ui-button ui-button--secondary" type="button">更新</button>
            </header>
            <ParticipantSessionCard
              start-time="10:00"
              track="Track A"
              title="Keynote: Product Vision"
              seat-label="受付中"
              seat-tone="open"
            />
            <ParticipantSessionCard
              start-time="11:30"
              track="Track B"
              title="API Design Clinic"
              seat-label="残りわずか"
              seat-tone="few"
              :reserved="true"
              :disabled="true"
            />
          </section>

          <aside class="ui-layout-stack">
            <ParticipantReservationPanel
              :reservations="[
                { id: 'keynote', title: 'Keynote: Product Vision', time: '10:00 | Track A' },
                { id: 'session-a1', title: 'API Design Clinic', time: '11:30 | Track B' }
              ]"
              :has-token="true"
              :registered="true"
              :registration-status-loaded="true"
            />
            <ParticipantQrPanel
              qr-code-payload="event-reservation://checkin?guestId=Guest-A12"
              :qr-code-image-url="qrImageUrl"
              qr-code-generation-status="ready"
              :reservations="[
                { id: 'keynote', title: 'Keynote: Product Vision', meta: '10:00 | Track A' },
                { id: 'session-a1', title: 'API Design Clinic', meta: '11:30 | Track B' }
              ]"
              :has-token="true"
            />
          </aside>
        </section>
      </main>
    `,
  }),
} satisfies Meta;

export default meta;
type Story = StoryObj<typeof meta>;

export const Mobile390: Story = {
  decorators: [fixedFrameDecorator('390px', '844px')],
};

export const Tablet834: Story = {
  decorators: [fixedFrameDecorator('834px', '1112px')],
};

export const Desktop1280: Story = {
  decorators: [fixedFrameDecorator('1280px', '800px')],
};
