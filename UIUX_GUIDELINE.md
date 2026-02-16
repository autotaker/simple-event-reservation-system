# UIUX_GUIDELINE

このリポジトリでUIUXデザイナーが実施すべき項目を整理したガイドです。

## 1. 目的

- 開発着手前に画面モックで実装意図を明確化する。
- マージ後にUX品質を検証し、ユーザーストーリー成立を担保する。

## 2. 使用ツール

- ワイヤーフレーム: `draw.io`（Desktop版を推奨）
- UIカタログ: `Storybook`（`frontend` 配下）
- ドキュメント公開: GitHub Pages（`/storybook/` 配下で公開）

## 3. 責任範囲

- デザイナーPR（モックPR）作成前後: 画面モックの作成、PR化、開発担当者への共有。
- デザイントークン（`core` / `semantic`）の設計・命名・更新方針の管理。
- マージ後: `main` ブランチでのUXテスト実施と結果報告。
- UX課題を再現可能な形で記録し、改善提案まで提示する。

## 4. デザイナーPR作成（ワイヤーフレーム・コンポーネントモック作成）

- Issueの受け入れ条件と対象ユーザーフローを確認する。
- `draw.io` で主要画面のワイヤーフレームを作成する
- ワイヤーフレームで確定した単位をStorybookコンポーネントモックへ落とし込む。
- モック作成時に既存デザイントークンで表現できない要素がある場合は、`core` / `semantic` トークンを追加してからモックへ反映する。
- 開発担当者と認識合わせを行い、実装前に合意を取る。
- モック成果物を「デザイナーPR」として作成し、レビュー後に先にマージする。
- デザイナーPRの下書きには `.github/pr_templates/ui-mock-pr-template.md` を使用できる（モック合意のために見出し再構成済み）。
- 開発担当者の実装PRは、デザイナーPRが `main` にマージされた後に作成する。

## 5. UIモック成果物規約（再現可能な作成手順）

UI/UX設計を含むIssueでは、以下を最低限の成果物セットとする。

### 5.1 LoFi（drawio）

- 目的: 情報構造・画面領域・状態遷移を合意する。
- 必須ファイル:
  - `docs/uiux/issue-<番号>-<テーマ>.drawio`
  - `docs/images/uiux/issue-<番号>-<テーマ>-p1.png`
  - `docs/images/uiux/issue-<番号>-<テーマ>-p2.png`
  - `docs/images/uiux/issue-<番号>-<テーマ>.png`（必要時は結合画像）
- ページ構成（最低）:
  - `p1`: Mobile（例: 390x844）
  - `p2`: Tablet（例: 834x1112）
- 生成コマンド（drawio-diagram-ops準拠）:
```bash
/Users/autotaker/.codex/skills/drawio-diagram-ops/scripts/render_drawio.py docs/uiux/issue-<番号>-<テーマ>.drawio --format png --pages all --outdir docs/images/uiux
/Users/autotaker/.codex/skills/drawio-diagram-ops/scripts/render_drawio.py docs/uiux/issue-<番号>-<テーマ>.drawio --format png --pages all --outdir docs/images/uiux --merge-png --merged-output docs/images/uiux/issue-<番号>-<テーマ>.png
```

### 5.2 HiFi（Storybook: 画面レイアウト）

- 目的: 実装前に視覚設計と操作導線を合意する。
- 必須ファイル:
  - `frontend/src/stories/<feature-hifi>/<ScreenLayout>.vue`
  - `frontend/src/stories/<feature-hifi>/<ScreenLayout>.stories.ts`
- 必須ストーリー:
  - 画面レイアウト（通常状態）
  - `loading` 状態
  - `success` 状態
  - `error` 状態
- 各ストーリーに想定デバイスを明記する（`parameters.docs.description.story`）。

### 5.3 HiFi（Storybook: 実装コンポーネント）

- 目的: 実装単位ごとのUI仕様を固定する。
- 必須ファイル（コンポーネントごと）:
  - `frontend/src/stories/<feature-hifi>/<ComponentMock>.vue`
  - `frontend/src/stories/<feature-hifi>/<ComponentMock>.stories.ts`
- 最低限作成するコンポーネント:
  - ヘッダー/導線要素
  - 主操作カード（例: セッションカード）
  - 補助パネル（例: 予約一覧）
  - マイページ関連要素（例: QR表示パネル）
- 各ストーリーに想定デバイスを明記する。

### 5.4 MDXカタログと引き渡しメモ

- Storybook MDXカタログを必須とし、以下を含める。
  - 画面レイアウトストーリー
  - 実装コンポーネントストーリー
  - 想定デバイス一覧（Primary/Secondary/Tertiary）
- 引き渡しメモを `docs/uiux/issue-<番号>-handoff.md` として作成し、以下を記載する。
  - LoFi成果物パス
  - HiFi成果物パス
  - 想定デバイス
  - 受け入れ条件との対応（CP1, CP2...）

### 5.5 デバイス想定（オンサイトイベント既定）

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅前後）

## 6. UIカタログ（Storybook）

- Storybookの起動:
  - `cd frontend && pnpm storybook`
- Storybookの静的ビルド:
  - `cd frontend && pnpm build-storybook`
- UIカタログは `MDX` 形式で作成し、仕様を文章で読める状態にする。
- モックコンポーネントの命名は `〜Mock` を必須とする。
  - 例: `SessionCardMock.vue`, `CheckInPanelMock.vue`
- `〜Mock` は仕様確認用の参照実装であり、開発担当はそれを基に実コンポーネントと実ストーリーを作成する。
- MDXには最低限以下を含める。
  - なぜそのデザインにしたか（設計意図）
  - ボタンや入力要素など、主要UI要素の役割
  - 各ストーリーで何を確認するか（確認観点）
- 実コンポーネントと実ストーリーの作成後は、原則 `〜Mock` を削除する。
- 初期追加・改修時は「見た目確認用の最小モック」を先に作り、実装詳細（composable等）はフロント担当に引き継ぐ。

## 7. マージ後（UXテスト）

- `main` を最新化した状態で対象導線を通しで確認する。
- モック意図と実装結果の差分を確認する。
- Storybook上で、`〜Mock` から派生した実コンポーネントのストーリーが担保されていることを確認する。
- 動作確認は UI/UX 担当が Playwright MCP を使って実導線を操作し、主要ストーリーに対応する挙動を確認する。
- 自動テスト（E2E/コンポーネントテスト）の結果は、開発担当が担保した証跡として参照する。
- 以下の観点でUXテストを行う。
  - 導線の分かりやすさ
  - 操作回数・操作負荷
  - 文言の明確さ
  - エラー時の理解しやすさ
  - フィードバックの即時性（一貫した表示/更新）
  - 実装構造の妥当性（モック責務が実コンポーネントへ昇格済みか）

### 7.1 実装構造チェック（必須）

UIUXレビュー時は、見た目と導線だけでなく、以下を必ず確認する。

1. 対象画面が単一Viewへの過度な集約になっていないか
2. デザイナーが合意したMock責務（例: TopBar / AccessDenied / Table / Editor）が実コンポーネントへ分割されているか
3. 画面Viewが「コンテナ（状態と合成）」として機能し、UI詳細責務が子コンポーネントに委譲されているか
4. Storybookが実コンポーネント基準で運用され、Mock参照が残存前提になっていないか

上記が未達の場合は、導線動作が成立していてもUX判定をPassにしない。

### 7.2 リリースブロッカー判定

以下に該当する場合は、UXレビューで `P1` を付与し、リリース不可として扱う。

- モック中心の構成から本番実装構成へ移行できていない
- 単一ViewにUI責務が集中し、合意済みの責務分割が実装に反映されていない
- 実コンポーネント基準のStorybook/検証導線が不足し、将来改修のUX回帰を安定検知できない

## 8. ローカル確認と公開

- `mkdocs serve` では `/storybook/` の同梱配信は行わない。
- ローカルでPages相当を確認する場合は以下を使用する。
  1. `cd frontend && pnpm build-storybook`
  2. `mkdocs build`
  3. `site/storybook/` に `frontend/storybook-static/` をコピー
  4. `site/` を静的サーバーで配信して確認する（例: `python3 -m http.server`）
- GitHub Pages公開は `.github/workflows/docs-pages.yml` で実施する。

## 9. 指摘フォーマット

- 指摘は日本語で記載する。
- 各指摘に以下を含める。
  - 優先度（`P1`/`P2`/`P3`）
  - 事象
  - 再現手順
  - 期待結果 / 実結果
  - 影響範囲
  - 改善提案
- 実装構造に関する指摘では、対象ファイルパス（View/Component/Story）を必ず明記する。

## 10. コミュニケーション（Issueコメント）

- UXレビュー結果のIssueコメントは以下テンプレートを使用する。

```md
## UXレビュー結果

- 対象Issue: #<番号>
- 実施日時: <YYYY-MM-DD HH:mm JST>
- 対象環境: <main / PRブランチ>
- 総合判定: <Pass/Fail>

### 確認チェックポイント

- <チェックポイント1>: <Pass/Fail>
- <チェックポイント2>: <Pass/Fail>

### 指摘事項

- [P<1-3>] <事象の要約>
  - 再現手順: <手順>
  - 期待結果: <期待>
  - 実結果: <実際>
  - 影響範囲: <影響>
  - 改善提案: <提案>

### 備考

- <補足 or なし>
```

- 指摘がない場合も `指摘事項: なし` を明記する。
- Fail判定時は、再確認が必要な条件と再テスト条件を `備考` に明記する。
- 実装構造未達を理由にFailとする場合は、以下を必ず明記する。
  - 「運用課題ではなく実装不足」であること
  - 期待する実装形（コンテナ + 実コンポーネント分割）
  - リリース再開条件（分割実装・Storybook切替・再UXテスト）

## 11. 完了条件

- モックが対象受け入れ条件をカバーしている。
- Storybookで受け入れ条件の判定に必要な状態が確認できる。
- `〜Mock` を基に作成された実コンポーネントのストーリーが存在し、UXテスト時に確認できる。
- 原則として `〜Mock` が整理（削除）され、運用対象が実コンポーネントへ統一されている。
- マージ後UXテスト結果が記録されている。
- 重要なUX課題（P1/P2）の対応方針が開発チームと合意できている。
- 重要UI（導線・権限制御・主要操作）で、実装構造チェック（7.1）がPassになっている。
