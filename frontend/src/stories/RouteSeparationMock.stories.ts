import type { Meta, StoryObj } from '@storybook/vue3-vite';
import RouteSeparationMock from './RouteSeparationMock.vue';

const meta = {
  title: 'Issue 30/RouteSeparationMock',
  component: RouteSeparationMock,
  tags: ['autodocs'],
  args: {
    persona: 'participant',
    state: 'default',
  },
  argTypes: {
    persona: {
      control: 'radio',
      options: ['participant', 'admin', 'forbidden'],
    },
    state: {
      control: 'radio',
      options: ['default', 'loading', 'success', 'forbidden'],
    },
  },
} satisfies Meta<typeof RouteSeparationMock>;

export default meta;

type Story = StoryObj<typeof meta>;

export const ParticipantDefault: Story = {};

export const ParticipantLoading: Story = {
  args: {
    state: 'loading',
  },
};

export const AdminDefault: Story = {
  args: {
    persona: 'admin',
  },
};

export const SeparationSuccess: Story = {
  args: {
    persona: 'participant',
    state: 'success',
  },
};

export const DirectAccessForbidden: Story = {
  args: {
    persona: 'forbidden',
    state: 'forbidden',
  },
};
