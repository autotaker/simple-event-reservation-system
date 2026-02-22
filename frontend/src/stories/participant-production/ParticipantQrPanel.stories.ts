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
  'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALMAAACzCAYAAADCFC3zAAAAAklEQVR4AewaftIAAAeLSURBVO3BUW4duZYAwUxC+99yTn8SRMN1XSPZ/YgTYf9gjAssxrjEYoxLLMa4xGKMS3zxL1T+porvovI3VZxUdhWfUHlSsVP5RMUbKn9LxW4xxiUWY1xiMcYlFmNc4osPVfwUle+gcqp4ovKk4qRyqtipnCp2KqeKU8UTlTdUnlQ8qfgpKr+yGOMSizEusRjjEl+8pPJGxRsqp4onKk8qTio7lTcqnlScVJ5UnCqeqJwqforKGxW/YzHGJRZjXGIxxiUWY1zii4tUnFR2FZ+oeENlV/FGxUllp/JdVHYV/2sWY1xiMcYlFmNcYjHGJb74H6ayq3ii8omKnconKnYqp4qdyicqdiqnip3KqeKJyqniv2wxxiUWY1xiMcYlvnip4m+r2Kk8qfiEypOK71DxRsWTipPKn1TxJyzGuMRijEssxrjEYoxLfPEhlf9FFTuVU8WTipPKqeJ3qZwqTiq7ipPKruITFTuVN1T+lsUYl1iMcYnFGJf44l9U3KriExU7lVPFk4o3VJ6ofBeVXcUnKv4rFmNcYjHGJRZjXGIxxiXsHxxUThU7lZ9S8VNUvkvFE5VTxRsqu4qTyhsVO5VTxUnlp1T8jsUYl1iMcYnFGJdYjHEJ+wcfUNlVnFROFTuVU8VO5btU7FQ+UbFT+UTFTuWNiicqb1Q8UflExU7lVLFTOVX8fy3GuMRijEssxriE/YMPqDypOKnsKk4qu4qTyhsVb6jsKn6KyicqfpfKGxWfUNlVnFR+QsVuMcYlFmNcYjHGJRZjXOKLf6FyqvgOKqeKncobFSeVJxVvqJwqdiqnijdUdhVPKt5Q+UTFk4qdyqni/2sxxiUWY1xiMcYl7B+8oPJTKj6h8qdUfELlO1ScVHYVJ5UnFW+o/JdU7BZjXGIxxiUWY1xiMcYl7B8cVN6oeKJyqtipnCqeqDyp+Ekqu4qTyhsVO5VTxU7lExU7lVPFE5VTxRsqu4pfWYxxicUYl1iMcYnFGJf44g+rOKm8obKrOKnsVE4VT1ROFW9U7FQ+obKreKPipLKrOKn8SRU7lV3FbjHGJRZjXGIxxiXsHxxUThU7lU9UfAeVJxVPVD5RsVP5KRUnlScVb6i8UfFE5VTxhsqvVOwWY1xiMcYlFmNcYjHGJb74FxUnlZ+isqt4Q+VUsav4hMobFd+h4qTyROU7VJxU3lB5o2Kn8iuLMS6xGOMSizEusRjjEl98o4qTyq7iVPGk4qSyqzip7Cq+S8VJ5XdVvKFyqtipnCqeqJwqnqicKt5Q2VX8ymKMSyzGuMRijEt88S9UThU/ReWnVDxReUPlVPGnVJxU3lB5ovKk4jtoMcbFGJdYjHGJxRiXWIxxiS9eqviTKk4qO5UnFZ+o2KmcKr5DxUnlicqp4onKSeUNlZ9SsVP5lcUYl1iMcYnFGJf44kMqf5PKqWKn8rep7CpOKk8qTipPVN6oeEPlicqu4qTy/7UY4xKLMS6xGOMS9g/GuMBijEssxrjEYoxL/B/6vrmNzo8/7AAAAABJRU5ErkJggg==';

export const WithQrCode: Story = {
  args: {
    qrCodePayload: 'event-reservation://checkin?guestId=Guest-A12',
    qrCodeImageUrl: qrImageUrl,
    qrCodeGenerationStatus: 'ready',
    reservations: [
      { id: 'keynote', title: 'Opening Keynote', meta: '09:00 | Keynote' },
      { id: 'session-a1', title: 'Session 1', meta: '10:30 | Track A' },
    ],
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
    reservations: [{ id: 'keynote', title: 'Opening Keynote', meta: '09:00 | Keynote' }],
    hasToken: true,
    disabled: false,
  },
};

export const Error: Story = {
  args: {
    qrCodePayload: 'event-reservation://checkin?guestId=Guest-A12&reservations=keynote',
    qrCodeImageUrl: '',
    qrCodeGenerationStatus: 'error',
    reservations: [
      {
        id: 'unknown-1',
        title: '不明なセッション',
        meta: '更新して最新情報を取得してください',
        fallback: true,
      },
    ],
    hasToken: true,
    disabled: false,
  },
};
