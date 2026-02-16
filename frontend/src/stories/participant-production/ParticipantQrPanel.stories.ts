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
  'https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=event-reservation%3A%2F%2Fcheckin%3FguestId%3DGuest-A12';

export const WithQrCode: Story = {
  args: {
    qrCodePayload: 'event-reservation://checkin?guestId=Guest-A12',
    qrCodeImageUrl: qrImageUrl,
    reservations: ['keynote', 'session-a1'],
    hasToken: true,
    disabled: false,
  },
};

export const Placeholder: Story = {
  args: {
    qrCodePayload: '',
    qrCodeImageUrl: qrImageUrl,
    reservations: [],
    hasToken: true,
    disabled: false,
  },
};

export const Disabled: Story = {
  args: {
    qrCodePayload: '',
    qrCodeImageUrl: qrImageUrl,
    reservations: [],
    hasToken: false,
    disabled: true,
  },
};
