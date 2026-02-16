# Issue #40 管理導線 (/admin) モック合意メモ

## 目的

- `/admin` 導線に管理機能を集約し、参加者導線との混在を防ぐ。
- 権限なしユーザーの `/admin` 直接アクセス時に、管理画面を表示せず理由提示と復帰導線を提供する。
- 実装担当が画面責務と状態遷移（loading/success/error）を同じ解釈で実装できる状態にする。

## モック成果物

- draw.io: `docs/uiux/issue-40-admin-route-guard.drawio`
- PNG: `docs/images/uiux/issue-40-admin-route-guard.png`
- Storybook Mock: `frontend/src/stories/AdminRouteGuardMock.vue`
- Storybook Stories: `frontend/src/stories/AdminRouteGuardMock.stories.ts`
- Storybook MDX: `frontend/src/stories/AdminRouteGuardCatalog.mdx`

## ユーザーフローチェックポイント

- CP1: 管理権限ありで `/admin` に遷移した場合、管理機能のみ表示される
- CP2: 権限判定中は操作を抑止し、判定中フィードバックが表示される
- CP3: 権限なしで `/admin` に直接アクセスした場合、管理画面は表示されず403導線で `/participant` へ戻れる

## 状態別の意図

### success（許可）

- 表示する要素:
  - 管理一覧
  - セッション作成/編集
  - CSV 出力
- 表示しない要素:
  - 参加者向け予約導線
  - マイページ導線

### loading（判定中）

- 判定完了まで主要操作ボタンを無効化する。
- 「権限を確認しています」フィードバックを表示する。

### error（拒否）

- `403 権限がありません` を表示する。
- 理由文を表示する（参加者権限では管理導線に入れない）。
- `/participant` へ戻る主導線を表示する。
- 管理画面本文や管理操作ボタンは描画しない。

## 実装への引き渡し

- 画面責務分離: `/admin` は管理操作専用とし、参加者向け機能を含めない。
- 直接アクセス制御: `/admin` URL 直打ちでも同一の拒否表示を適用する。
- フィードバック整合: loading/error の文言と表示タイミングを固定し、画面ごとのバラつきを避ける。

## 非対象

- `/participant` 側の予約体験改善
- 認証方式の刷新
- E2E シナリオ更新
