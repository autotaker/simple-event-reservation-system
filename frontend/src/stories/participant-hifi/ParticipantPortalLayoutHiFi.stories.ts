import type { Meta, StoryObj } from '@storybook/vue3-vite';
import ParticipantPortalLayoutHiFi from './ParticipantPortalLayoutHiFi.vue';

const meta = {
  title: 'US-12-1 Participant HiFi/ParticipantPortalLayout',
  component: ParticipantPortalLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof ParticipantPortalLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileOnsiteDefault: Story = {
  args: { mode: 'default', isMobile: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
    layout: 'centered',
  },
};

export const MobileOnsiteLoading: Story = {
  args: { mode: 'loading', isMobile: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (通信待ち状態)' } },
    layout: 'centered',
  },
};

export const TabletKioskView: Story = {
  args: { mode: 'success', isMobile: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
    layout: 'centered',
  },
};

export const DesktopPreEventCheck: Story = {
  args: { mode: 'error', isMobile: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)' } },
    layout: 'fullscreen',
  },
};
