# Issue #31 CSS基盤モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #31 の受け入れ条件に沿って、共通CSS基盤の仕様境界を実装前に固定する。
- 「初版はUIUX作成、保守運用は開発担当」という分担で、仕様と実装のズレを防ぐ。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-31-css-foundation.drawio`
- PNG(page1): `docs/images/uiux/issue-31-css-foundation-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-31-css-foundation-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-31-css-foundation.png`

### HiFi（Storybook）

- Layout: `frontend/src/stories/foundation-hifi/FoundationLayoutHiFi.vue`
- Layout Stories: `frontend/src/stories/foundation-hifi/FoundationLayoutHiFi.stories.ts`
- Component Stories:
  - `frontend/src/stories/foundation-hifi/PageShellMock.stories.ts`
  - `frontend/src/stories/foundation-hifi/BaseButtonMock.stories.ts`
  - `frontend/src/stories/foundation-hifi/BaseFieldMock.stories.ts`
  - `frontend/src/stories/foundation-hifi/BaseTableMock.stories.ts`
  - `frontend/src/stories/foundation-hifi/StatusMessageMock.stories.ts`
- Storybook MDX: `frontend/src/stories/foundation-hifi/FoundationHiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## UXチェックポイントとステータス

- CP1: CSS基盤（余白、階層、状態表現）の適用方針が定義される - Pass
- CP2: loading / success / error / disabled が視覚的に識別可能 - Pass
- CP3: モバイルで主要操作が崩れない構成が定義される - Pass

## CSS仕様契約（実装引き渡し）

- クラス命名
  - レイアウト: `.ui-shell-*`
  - コンポーネント: `.ui-button-*`, `.ui-field-*`, `.ui-table-*`, `.ui-status-*`
- 状態
  - `loading`, `success`, `error`, `disabled` を必須状態とする。
- トークン
  - `frontend/src/styles/tokens.css` のセマンティックトークンのみ使用する。
- 禁止事項
  - 直値カラー指定
  - 画面ごとの独自 disabled 見た目
  - エラー表示位置の画面ごとの不統一

## 実装担当への依頼

- UIUX作成の初版仕様を基準に、開発担当は本番画面（participant / admin / operator）へ適用する。
- 変更時は Storybook と handoff を同時更新し、契約差分を追跡可能にする。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #31 実装PRへ進行可能）
