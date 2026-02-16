# Issue #39 参加者導線モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #39 の受け入れ条件に沿って `/participant` 導線の責務を固定し、実装時に管理UI混在を防ぐ。
- 「モック = 非実装」ではなく、UIレイアウトと視覚設計の合意を先に完了させる。

## モック成果物

- draw.io: `docs/uiux/issue-39-participant-flow.drawio`
- PNG: `docs/images/uiux/issue-39-participant-flow.png`
- Storybook Mock: `frontend/src/stories/ParticipantFlowWireframeMock.vue`
- Storybook Stories: `frontend/src/stories/ParticipantFlowWireframeMock.stories.ts`
- Storybook MDX: `frontend/src/stories/ParticipantFlowWireframeCatalog.mdx`

## ユーザーフローチェックポイントとステータス

- CP1: `/participant` で参加者機能（セッション一覧/予約一覧/マイページ）が動作する - Pass（3ペイン構成で定義）
- CP2: 参加者画面に管理機能が描画されない - Pass（Hero内の非表示チップで明示）
- CP3: 既存参加者フロー（予約/取消/マイページ表示）が維持される - Pass（状態別フィードバックを定義）

## モック概要（実装引き渡し）

- 画面は `Hero + セッション一覧 + 予約一覧 + マイページ + 状態フィードバック` で構成。
- ヒーロー領域で「参加者機能の対象」と「管理機能の除外」を同時に提示する。
- 予約操作の状態は `loading / success / error` を画面下部に集約し、見落としを防ぐ。
- モバイル幅では3ペインを縦積みに切り替えるレスポンシブ方針を含める。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #39 の実装PRへ進行可能）
