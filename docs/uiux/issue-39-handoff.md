# Issue #39 参加者導線モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #39 の受け入れ条件に沿って `/participant` 導線の責務を固定し、実装時に管理UI混在を防ぐ。

## モック成果物

- draw.io: `docs/uiux/issue-39-participant-flow.drawio`
- PNG: `docs/images/uiux/issue-39-participant-flow.png`
- Storybook Mock: `frontend/src/stories/ParticipantFlowWireframeMock.vue`
- Storybook Stories: `frontend/src/stories/ParticipantFlowWireframeMock.stories.ts`
- Storybook MDX: `frontend/src/stories/ParticipantFlowWireframeCatalog.mdx`

## ユーザーフローチェックポイントとステータス

- CP1: `/participant` で参加者機能（セッション一覧/予約一覧/マイページ）が動作する - Pass（モック定義済み）
- CP2: 参加者画面に管理機能が描画されない - Pass（除外機能を明示）
- CP3: 既存参加者フロー（予約/取消/マイページ表示）が維持される - Pass（成功/エラー状態を含めて定義）

## モック概要（実装引き渡し）

- 参加者ホームは「参加者向け操作だけを完結させる1画面」として定義。
- `loading` / `success` / `error` の3状態をUX判定の最小セットとして定義。
- 予約と取消は同一画面内で完結し、操作結果フィードバックを即時表示する前提。
- 管理機能（管理一覧、作成/編集、CSV出力）は `/participant` に描画しないことを明示。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #39 の実装PRへ進行可能）
