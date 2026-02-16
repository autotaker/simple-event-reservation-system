# Issue #40 管理導線モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- `/admin` に管理機能（管理一覧・作成/編集・CSV出力）を集約し、参加者導線と責務を分離する。
- 権限なしの `/admin` 直接アクセス時に、管理画面を表示せず理由と復帰導線を提示する仕様を固定する。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-40-admin-flow.drawio`
- PNG(page1): `docs/images/uiux/issue-40-admin-flow-p1.png`（Admin authorized）
- PNG(page2): `docs/images/uiux/issue-40-admin-flow-p2.png`（Admin forbidden）
- PNG(page3): `docs/images/uiux/issue-40-admin-flow-p3.png`（Permission checking）
- PNG(merged): `docs/images/uiux/issue-40-admin-flow.png`

### HiFi（Storybook Mock）

- Mock Component: `frontend/src/stories/admin-flow/AdminRouteGuardMock.vue`
- Stories: `frontend/src/stories/admin-flow/AdminRouteGuardMock.stories.ts`
- Storybook MDX: `frontend/src/stories/admin-flow/AdminFlowCatalog.mdx`

## ユーザーフローチェックポイントとステータス

- CP1: `/admin` で管理機能が動作する - Pass（AdminAuthorized で責務定義済み）
- CP2: 権限なしで `/admin` へ直接アクセスしても管理画面を表示しない - Pass（AdminForbidden で管理UI非表示を固定）
- CP3: 拒否時にユーザーへ理由が分かるフィードバックがある - Pass（403理由文と復帰ボタンを定義）

## モック概要（実装引き渡し）

- Authorized: 管理機能のみ描画し、参加者機能は描画しない。
- Forbidden: 403理由表示 + `participant に戻る` のみ活性化し、管理本体は非表示。
- Checking: 権限判定中は管理操作ボタンを無効化し、判定完了まで待機。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #40 実装PRへ進行可能）
