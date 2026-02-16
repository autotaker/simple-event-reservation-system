# Issue #40 管理導線モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #40 の受け入れ条件に沿って `/admin` 導線の情報設計と権限外アクセス時のUXを固定し、実装時の解釈ブレを防ぐ。
- `docs/review/08-sprint-review-result.md` の「参加者導線と運営導線の分離」「拒否時の理由提示」を #40 スコープで具体化する。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-40-admin-flow.drawio`
- PNG(page1): `docs/images/uiux/issue-40-admin-flow-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-40-admin-flow-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-40-admin-flow.png`

### HiFi（Storybook）

- Layout: `frontend/src/stories/admin-hifi/AdminPortalLayoutHiFi.vue`
- Layout Stories: `frontend/src/stories/admin-hifi/AdminPortalLayoutHiFi.stories.ts`
- Component Stories:
  - `frontend/src/stories/admin-hifi/AdminTopBarMock.stories.ts`
  - `frontend/src/stories/admin-hifi/AdminSessionTableMock.stories.ts`
  - `frontend/src/stories/admin-hifi/AdminSessionEditorMock.stories.ts`
  - `frontend/src/stories/admin-hifi/AdminCsvExportPanelMock.stories.ts`
  - `frontend/src/stories/admin-hifi/AdminAccessDeniedPanelMock.stories.ts`
- Storybook MDX: `frontend/src/stories/admin-hifi/AdminHiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## ユーザーフローチェックポイントとステータス

- CP1: `/admin` で管理機能（管理一覧・作成/編集・CSV出力）が動作する - Pass（LoFi/HiFiで管理導線を定義済み）
- CP2: 権限なし状態で `/admin` へ直接アクセスしても管理画面を表示しない - Pass（`error` 状態で拒否パネルのみ表示）
- CP3: 拒否時にユーザーへ理由が分かるフィードバックがある - Pass（403理由 + `/participant` 復帰導線を定義）

## モック概要（実装引き渡し）

- 管理導線は「一覧」「編集」「出力」を同一画面で完結させ、操作対象を明確化する。
- 直接アクセス制御は `loading`（判定中）と `error`（拒否）を分離し、待機状態と拒否状態を混同しない。
- `error` では管理コンテンツを描画せず、拒否理由と参加者導線への復帰操作を優先表示する。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #40 実装PRへ進行可能）
