import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminAuthDeniedPanelMock from './AdminAuthDeniedPanelMock.vue';

const meta = {
  title: 'US-15 Admin Auth HiFi/Components/AdminAuthDeniedPanel',
  component: AdminAuthDeniedPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminAuthDeniedPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Expired401State: Story = {
  args: {
    heading: 'トークンの有効期限が切れました',
    message: '管理APIが401を返しました。sessionStorageのトークンを破棄し、再ログインしてください。',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（期限切れ401）' } },
  },
};

export const Revoked401State: Story = {
  args: {
    heading: 'トークンは失効済みです',
    message:
      'ログアウト済み、または失効されたトークンです。再ログインして新しいトークンを発行してください。',
  },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン（失効401）' } },
  },
};

export const Invalid401State: Story = {
  args: {
    heading: '認証情報を確認できません',
    message:
      '未認証、または不正なトークンです。sessionStorageのトークンを破棄し、再ログインしてください。',
  },
  parameters: {
    docs: {
      description: { story: '想定デバイス: 参加者スマートフォン（未認証/不正トークン401）' },
    },
  },
};
