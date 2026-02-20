import type { Meta, StoryObj } from '@storybook/vue3-vite';
import { ref } from 'vue';
import AdminAccessDeniedPanel from '../../components/admin/AdminAccessDeniedPanel.vue';
import AdminAccessGate from '../../components/admin/AdminAccessGate.vue';
import AdminCsvExportPanel from '../../components/admin/AdminCsvExportPanel.vue';
import AdminSessionEditor from '../../components/admin/AdminSessionEditor.vue';
import AdminSessionTable from '../../components/admin/AdminSessionTable.vue';
import AdminTopBar from '../../components/admin/AdminTopBar.vue';
import { viewportParams } from './viewportPresets';

const meta = {
  title: 'US-13 CSS Foundation Production/AdminFlowResponsive',
  tags: ['autodocs'],
  render: () => ({
    components: {
      AdminTopBar,
      AdminAccessGate,
      AdminAccessDeniedPanel,
      AdminSessionTable,
      AdminSessionEditor,
      AdminCsvExportPanel,
    },
    setup() {
      const token = ref('admin-token');
      const createForm = ref({
        sessionId: '',
        title: 'New Session',
        startTime: '16:30',
        track: 'Track C',
        capacity: '120',
      });
      return { token, createForm };
    },
    template: `
      <main class="ui-shell ui-shell--admin">
        <AdminTopBar
          event-name="Tokyo Product Summit 2026"
          section-label="運営管理ポータル /admin"
          admin-name="Ops Admin"
          return-to="/participant"
          return-label="参加者画面へ戻る"
        />

        <AdminAccessGate v-model="token" />
        <p class="ui-status ui-status--success">管理者トークンを確認しました。</p>

        <section class="ui-layout-stack">
          <h2 style="margin:0;">セッション管理（運営）</h2>
          <div class="ui-layout-split">
            <AdminSessionTable
              :sessions="[
                {
                  sessionId: 'S-101',
                  title: 'Design Tokens Overview',
                  startTime: '10:00',
                  track: 'Track A',
                  capacity: 180,
                  reservedCount: 72
                },
                {
                  sessionId: 'S-204',
                  title: 'UX Feedback Ops',
                  startTime: '13:30',
                  track: 'Track B',
                  capacity: 150,
                  reservedCount: 144
                }
              ]"
              :disabled="false"
            />
            <aside class="ui-layout-stack">
              <AdminSessionEditor
                heading="新規作成"
                :form="createForm"
                submit-label="セッション作成"
                :can-submit="true"
                @update:form="(nextForm) => (createForm = nextForm)"
              />
              <AdminCsvExportPanel :disabled="false" />
            </aside>
          </div>
        </section>
      </main>
    `,
  }),
} satisfies Meta;

export default meta;
type Story = StoryObj<typeof meta>;

export const Mobile390: Story = {
  parameters: viewportParams('mobile390'),
};

export const Tablet834: Story = {
  parameters: viewportParams('tablet834'),
};

export const Desktop1280: Story = {
  parameters: viewportParams('desktop1280'),
};

export const AccessDenied: Story = {
  parameters: viewportParams('mobile390'),
  render: () => ({
    components: { AdminAccessDeniedPanel },
    template: `
      <main class="ui-shell ui-shell--admin">
        <AdminAccessDeniedPanel message="管理権限がないため /admin の管理画面を表示できません。" />
      </main>
    `,
  }),
};
