import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantSessionTimetableMock from './ParticipantSessionTimetableMock.vue';

const meta = {
  title: 'US-14 Participant Timetable HiFi/ParticipantSessionTimetableMock',
  component: ParticipantSessionTimetableMock,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantSessionTimetableMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileDefault: Story = {
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
    layout: 'centered',
  },
};

export const Loading: Story = {
  args: {
    disabled: true,
    feedback: '予約情報を更新中です...',
    feedbackTone: 'loading',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 通信待ちで操作不可' } },
    layout: 'centered',
  },
};

export const Success: Story = {
  args: {
    feedback: '11:00 Track A の予約を確定しました',
    feedbackTone: 'success',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
    layout: 'centered',
  },
};

export const Error: Story = {
  args: {
    feedback: '予約に失敗しました。時間を空けて再試行してください',
    feedbackTone: 'error',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)' } },
    layout: 'fullscreen',
  },
};
