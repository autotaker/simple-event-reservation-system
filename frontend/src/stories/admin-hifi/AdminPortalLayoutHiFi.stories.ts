import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminPortalLayoutHiFi from './AdminPortalLayoutHiFi.vue';

const meta = {
  title: 'US-12-2 Admin HiFi/AdminPortalLayout',
  component: AdminPortalLayoutHiFi,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminPortalLayoutHiFi>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileAdminDefault: Story = {
  args: { mode: 'default' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(390x844)で誤って/adminへ遷移した場合を含む基準レイアウト' } },
    layout: 'centered',
  },
};

export const MobileAdminLoading: Story = {
  args: { mode: 'loading' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(権限判定/保存処理の待機状態)' } },
    layout: 'centered',
  },
};

export const TabletAdminSuccess: Story = {
  args: { mode: 'success' },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット(834x1112)で管理操作成功後' } },
    layout: 'centered',
  },
};

export const MobileForbiddenError: Story = {
  args: { mode: 'error' },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォンで権限なし/admin直接アクセス時' } },
    layout: 'centered',
  },
};
