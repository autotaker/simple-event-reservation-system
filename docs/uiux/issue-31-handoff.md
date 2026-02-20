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

## ブレークポイント定義（公式）

- `mobile`: `max-width: 767px`
- `tablet`: `min-width: 768px` かつ `max-width: 1023px`
- `desktop`: `min-width: 1024px`
- 備考: 390 / 834 / 1280 は検証代表値であり、実装判定は上記境界値を優先する。

## UXチェックポイントとステータス

- CP1: CSS基盤（余白、階層、状態表現）の適用方針が定義される - Pass
- CP2: loading / success / error / disabled が視覚的に識別可能 - Pass
- CP3: モバイルで主要操作が崩れない構成が定義される - Pass

## CSS仕様契約（実装引き渡し）

- クラス命名
  - レイアウト: `.ui-shell`, `.ui-shell--compact`
  - コンポーネント: `.ui-button`, `.ui-button--primary`, `.ui-button--secondary`
  - コンポーネント: `.ui-field`, `.ui-field--error`
  - コンポーネント: `.ui-table`, `.ui-table__col--id`, `.ui-table__col--title`, `.ui-table__col--action`
  - コンポーネント: `.ui-status`, `.ui-status--loading`, `.ui-status--success`, `.ui-status--error`
- 状態
  - `loading`, `success`, `error`, `disabled` を基盤必須状態とする。
  - コンポーネント別の必須状態:
    - `StatusMessage`: `loading`, `success`, `error`（`disabled` 非対象）
    - `BaseButton`: `primary`, `secondary`, `disabled`
    - `BaseField`: `default`, `error`, `disabled`
    - `BaseTable`: `default`（必要時に `loading`/`error` はテーブル外の `StatusMessage` で表現）
    - `PageShell`: `default`, `compact`（レスポンシブ）
- セカンダリーボタン意味論
  - `secondary` は「通常補助操作（更新、戻る、キャンセル）」を指す。
  - 注意喚起/危険操作は `secondary` を使わず、別トーン（将来 `danger`）で定義する。
- トークン
  - 実装コードは `frontend/src/styles/tokens.css` のトークン参照を基本とする。
  - 例外: 検証代表値（`390`, `834`, `1280`）や未定義トークン補完は handoff 明記のうえ暫定許容。
  - 暫定許容を使った場合は実装PR内でトークン化計画を記載する。
- 禁止事項
  - 直値カラー指定
  - 画面ごとの独自 disabled 見た目
  - エラー表示位置の画面ごとの不統一

## CSSファイル分割対応表（Issue #31コメント準拠）

- `frontend/src/styles/base.css`
  - タイポグラフィ、`body` 既定、要素リセット、フォーカス可視化の共通土台
- `frontend/src/styles/layout.css`
  - `.ui-shell` 系のレイアウト、`section` 間余白、ブレークポイントごとの段組み
- `frontend/src/styles/components.css`
  - `.ui-button` / `.ui-field` / `.ui-table` など部品の既定見た目
- `frontend/src/styles/state.css`
  - `.ui-status--*`、`disabled`、`loading/success/error` の状態表現

## モバイルテーブル運用ルール

- 優先表示列: `ID` > `Title` > `Action`
- 最小表示要件:
  - `ID` と `Action` は常時表示
  - `Title` は1行省略（ellipsis）を許容
- 横スクロールを許容する場合:
  - 操作列（`Action`）の最小幅を確保し、誤タップを防ぐ
  - 情報欠落を避けるため、詳細確認導線（例: 編集モーダル）を別途提供する

## 実装担当への依頼

- UIUX作成の初版仕様を基準に、開発担当は本番画面（participant / admin / operator）へ適用する。
- 変更時は Storybook と handoff を同時更新し、契約差分を追跡可能にする。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（本モック合意後に #31 実装PRへ進行可能）
