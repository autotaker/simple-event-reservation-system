import type { Meta, StoryObj } from '@storybook/vue3-vite';
import WireframeCard from './WireframeCard.vue';

const meta = {
  title: 'Wireframe/WireframeCard',
  component: WireframeCard,
  tags: ['autodocs'],
  args: {
    title: 'セッション予約',
    description: 'セッションを選択して予約を行います。',
    ctaLabel: '予約する',
    status: 'default',
  },
} satisfies Meta<typeof WireframeCard>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};

export const Loading: Story = {
  args: {
    status: 'loading',
  },
};

export const Success: Story = {
  args: {
    status: 'success',
    description: '予約が完了しました。マイページで確認できます。',
  },
};

export const Error: Story = {
  args: {
    status: 'error',
    description: '同時間帯の予約はできません。別のセッションを選んでください。',
  },
};
