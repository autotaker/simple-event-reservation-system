# Issue #42 導線分離モック合意メモ

## 目的

- participant / admin / forbidden の3導線を実装前に固定し、#39/#40 の解釈ブレを防ぐ。

## モック成果物

- draw.io: `docs/uiux/issue-42-flow-separation.drawio`
- PNG: `docs/images/uiux/issue-42-flow-separation.png`
- Storybook Mock: `frontend/src/stories/FlowSeparationWireframeMock.vue`
- Storybook Stories: `frontend/src/stories/FlowSeparationWireframeMock.stories.ts`
- Storybook MDX: `frontend/src/stories/FlowSeparationWireframeCatalog.mdx`

## ユーザーフローチェックポイント

- CP1: `/participant` では参加者機能のみ表示する
- CP2: `/admin` では管理機能のみ表示する
- CP3: 権限なしで `/admin` 直接アクセス時は 403 と復帰導線を表示し、管理画面は出さない

## #39 / #40 への引き渡し境界

### #39（参加者導線）

- 対象:
  - `/participant` 画面の実装
  - 参加者向け機能（セッション一覧、予約一覧、マイページ）の配置
- 非対象:
  - `/admin` 画面実装
  - `/admin` 直接アクセス時の拒否表示

### #40（管理導線 + アクセス制御）

- 対象:
  - `/admin` 画面の実装
  - 管理向け機能（管理一覧、作成/編集、CSV出力）の配置
  - 権限なし `/admin` 直接アクセス時の拒否表示と `/participant` への復帰導線
- 非対象:
  - `/participant` 側の予約フロー改変

## 状態定義（UX判定に必要な最小セット）

- loading: 権限判定中で遷移ボタンを無効化
- success: 許可された導線に遷移可能
- error: 権限外アクセスで理由を明示
