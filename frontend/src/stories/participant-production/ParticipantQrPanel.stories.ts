import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantQrPanel from '../../components/participant/ParticipantQrPanel.vue';

const meta = {
  title: 'US-12-1 Participant Production/Components/QrPanel',
  component: ParticipantQrPanel,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantQrPanel>;

export default meta;
type Story = StoryObj<typeof meta>;

const qrImageUrl =
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII=';

export const WithQrCode: Story = {
  args: {
    qrCodePayload: 'event-reservation://checkin?guestId=Guest-A12',
    qrCodeImageUrl: qrImageUrl,
    qrCodeGenerationStatus: 'ready',
    reservations: ['keynote', 'session-a1'],
    hasToken: true,
    disabled: false,
  },
};

export const Placeholder: Story = {
  args: {
    qrCodePayload: '',
    qrCodeImageUrl: qrImageUrl,
    qrCodeGenerationStatus: 'idle',
    reservations: [],
    hasToken: true,
    disabled: false,
  },
};

export const Disabled: Story = {
  args: {
    qrCodePayload: '',
    qrCodeImageUrl: qrImageUrl,
    qrCodeGenerationStatus: 'idle',
    reservations: [],
    hasToken: false,
    disabled: true,
  },
};

export const Generating: Story = {
  args: {
    qrCodePayload: 'event-reservation://checkin?guestId=Guest-A12&reservations=keynote',
    qrCodeImageUrl: '',
    qrCodeGenerationStatus: 'generating',
    reservations: ['keynote'],
    hasToken: true,
    disabled: false,
  },
};

export const Error: Story = {
  args: {
    qrCodePayload: 'event-reservation://checkin?guestId=Guest-A12&reservations=keynote',
    qrCodeImageUrl: '',
    qrCodeGenerationStatus: 'error',
    reservations: ['keynote'],
    hasToken: true,
    disabled: false,
  },
};
