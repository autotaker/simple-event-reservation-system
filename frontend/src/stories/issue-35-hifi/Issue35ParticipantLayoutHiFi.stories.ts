import type { Meta, StoryObj } from '@storybook/vue3-vite';
import Issue35ParticipantLayoutHiFi from './Issue35ParticipantLayoutHiFi.vue';

const meta = {
  title: 'Issue-35 HiFi/Layout/ParticipantReadableReservation',
  component: Issue35ParticipantLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof Issue35ParticipantLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: { mode: 'default', fallback: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)。通常表示。' } },
    layout: 'centered',
  },
};

export const Loading: Story = {
  args: { mode: 'loading', fallback: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン。更新中状態。' } },
    layout: 'centered',
  },
};

export const Success: Story = {
  args: { mode: 'success', fallback: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)。更新成功状態。' } },
    layout: 'centered',
  },
};

export const Error: Story = {
  args: { mode: 'error', fallback: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)。取得失敗状態。' } },
    layout: 'fullscreen',
  },
};

export const FallbackText: Story = {
  args: { mode: 'default', fallback: true },
  parameters: {
    docs: { description: { story: '表示データ不足時は `不明なセッション` と更新誘導を表示。' } },
    layout: 'centered',
  },
};
