import type { Meta, StoryObj } from '@storybook/vue3-vite';
import FoundationLayoutHiFi from './FoundationLayoutHiFi.vue';

const meta = {
  title: 'US-13 Foundation HiFi/FoundationLayout',
  component: FoundationLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof FoundationLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileDefault: Story = {
  args: { mode: 'default', isMobile: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
    layout: 'centered',
  },
};

export const MobileLoading: Story = {
  args: { mode: 'loading', isMobile: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (通信待ち状態)' } },
    layout: 'centered',
  },
};

export const TabletSuccess: Story = {
  args: { mode: 'success', isMobile: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
    layout: 'centered',
  },
};

export const DesktopError: Story = {
  args: { mode: 'error', isMobile: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 来場前PC確認 (1280幅)' } },
    layout: 'fullscreen',
  },
};
