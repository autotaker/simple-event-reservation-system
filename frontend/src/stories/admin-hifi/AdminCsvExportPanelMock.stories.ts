import type { Meta, StoryObj } from '@storybook/vue3-vite';
import AdminCsvExportPanelMock from './AdminCsvExportPanelMock.vue';

const meta = {
  title: 'US-12-2 Admin HiFi/Components/CsvExportPanel',
  component: AdminCsvExportPanelMock,
  tags: ['autodocs'],
} satisfies Meta<typeof AdminCsvExportPanelMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileExportPanel: Story = {
  args: { disabled: false },
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン(通常出力)' } },
  },
};

export const TabletExportPanelLoading: Story = {
  args: { disabled: true },
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット(出力待機)' } },
  },
};
