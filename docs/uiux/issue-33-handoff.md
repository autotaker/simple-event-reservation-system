# Issue #33 管理者認証モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #33 の受け入れ条件に沿って、固定トークン依存から「ログイン + 短命トークン + 失効」へ移行する管理者導線を合意する。
- 期限切れ・失効時の再認証導線と、運用時の失効操作（発行/失効）の理解負荷を下げる。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-33-admin-auth-flow.drawio`
- PNG(page1): `docs/images/uiux/issue-33-admin-auth-flow-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-33-admin-auth-flow-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-33-admin-auth-flow.png`

### HiFi（Storybook）

- Layout: `frontend/src/stories/admin-auth-hifi/AdminAuthLayoutHiFi.vue`
- Layout Stories: `frontend/src/stories/admin-auth-hifi/AdminAuthLayoutHiFi.stories.ts`
- Component Stories:
  - `frontend/src/stories/admin-auth-hifi/AdminAuthTopBarMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminLoginCardMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminTokenStatusPanelMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminSessionRevocationPanelMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminAuthDeniedPanelMock.stories.ts`
- Storybook MDX: `frontend/src/stories/admin-auth-hifi/AdminAuthHiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## ユーザーフローチェックポイントとステータス

- CP1: 正規管理者がログイン後に管理API利用可能になる - Pass（`default/success` 状態で認証済み導線を定義）
- CP2: 期限切れまたは失効済みトークンで管理APIが拒否される - Pass（`error` 状態で再ログイン導線を定義）
- CP3: 失効運用（全失効/選択失効）を実行できる - Pass（失効パネルで操作と監査メモ入力を定義）

## モック概要（実装引き渡し）

- 固定トークン入力UIは提供せず、管理者ログインカードを導線の起点にする。
- 認証後はトークン寿命を常時可視化し、更新・失効の操作結果を同画面で即時フィードバックする。
- `error` 状態では「期限切れ/失効済みで拒否された理由」と「再ログイン」への復帰導線を必須表示にする。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #33 の実装PRへ進行可能）
