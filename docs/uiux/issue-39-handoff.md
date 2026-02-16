# Issue #39 参加者導線モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #39 の受け入れ条件に沿って `/participant` 導線の責務を固定し、実装時に管理UI混在を防ぐ。
- LoFi と HiFi を分離し、構造合意と見た目合意を別レイヤーで明確化する。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-39-participant-flow.drawio`
- PNG(page1): `docs/images/uiux/issue-39-participant-flow-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-39-participant-flow-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-39-participant-flow.png`

### HiFi（Storybook）

- Layout: `frontend/src/stories/participant-hifi/ParticipantPortalLayoutHiFi.vue`
- Layout Stories: `frontend/src/stories/participant-hifi/ParticipantPortalLayoutHiFi.stories.ts`
- Component Stories:
  - `frontend/src/stories/participant-hifi/ParticipantTopBarMock.stories.ts`
  - `frontend/src/stories/participant-hifi/ParticipantSessionCardMock.stories.ts`
  - `frontend/src/stories/participant-hifi/ParticipantReservationPanelMock.stories.ts`
  - `frontend/src/stories/participant-hifi/ParticipantQrPanelMock.stories.ts`
- Storybook MDX: `frontend/src/stories/participant-hifi/ParticipantHiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## ユーザーフローチェックポイントとステータス

- CP1: `/participant` で参加者機能（セッション一覧/予約一覧/マイページ）が動作する - Pass（LoFi/HiFi 両方で構成定義済み）
- CP2: 参加者画面に管理機能が描画されない - Pass（画面定義に管理UIなし）
- CP3: 既存参加者フロー（予約/取消/マイページ表示）が維持される - Pass（状態別ストーリーを定義）

## モック概要（実装引き渡し）

- LoFi: 情報構造と領域分割を先に合意する。
- HiFi: 画面レイアウトと実装コンポーネントを分離して合意する。
- 状態表現: `default / loading / success / error` をレイアウトストーリーで担保する。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #39 の実装PRへ進行可能）
