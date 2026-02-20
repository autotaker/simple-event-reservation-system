import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantTimetableLayoutHiFi from './ParticipantTimetableLayoutHiFi.vue';

const meta = {
  title: 'US-14 Participant Timetable HiFi/ParticipantTimetableLayout',
  component: ParticipantTimetableLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantTimetableLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileDefault: Story = {
  args: { mode: 'default', isMobile: true },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン。比較→判断→予約→状態反映を1画面で完結。',
      },
    },
    layout: 'centered',
  },
};

export const MobileLoading: Story = {
  args: { mode: 'loading', isMobile: true },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 参加者スマートフォン。予約通信中は対象セルを含め操作を一時停止。',
      },
    },
    layout: 'centered',
  },
};

export const TabletSuccess: Story = {
  args: { mode: 'success', isMobile: false },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 受付補助タブレット。成功時はセル状態と予約一覧を更新。',
      },
    },
    layout: 'centered',
  },
};

export const DesktopError: Story = {
  args: { mode: 'error', isMobile: false },
  parameters: {
    docs: {
      description: {
        story: '想定デバイス: 来場前PC確認。制約違反や通信失敗時のエラー表示を確認。',
      },
    },
    layout: 'fullscreen',
  },
};
