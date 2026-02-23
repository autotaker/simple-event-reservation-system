# スプリントレビュー準備ランブック（再現手順）

この手順は、毎スプリントで同じ成果物を再現生成するための標準フローです。  
成果物はすべてリポジトリ相対パスで管理します。

## 0. 前提

- 実行場所: リポジトリルート
- 必須ツール:
  - `gh`（GitHub CLI、`gh auth login` 済み）
  - `jq`
  - `pnpm`
  - `java` / `gradle`
  - `python3`（`lizard` 実行用）
- ローカル起動前提:
  - `README.md` の「🚀 ローカル開発環境構築」に従う
  - DBはローカル PostgreSQL 17
  - Backendは `--spring.profiles.active=local`
  - `backend/src/main/resources/application-local.yml` はローカル専用（コミットしない）

## 1. スプリント対象Issue一覧を確定

1. スプリントで `closed` したIssue番号を確定する（例: `28,29,30,...`）。
2. 一覧を記録する（例: `reports/issue_numbers_current_sprint.txt`）。

完了条件:
- 対象Issue番号の重複なしリストがあること。

## 2. Issue詳細JSONを生成

`reports/generate_issue_detail_json.sh` を使って、Issue本文/コメント/関連PR情報を取得する。

```bash
./reports/generate_issue_detail_json.sh \
  --issues "28,29,30,31,32,36,37,39,40,41,42,..." \
  --output ./reports/current_sprint_closed_issues_detail.json
```

完了条件:
- `reports/current_sprint_closed_issues_detail.json` が生成されること。

## 3. 可視化HTMLで確認

1. `reports/current_sprint_closed_issues_viewer.html` をブラウザで開く。
2. `current_sprint_closed_issues_detail.json` をインポートする。
3. 以下を確認する:
  - Issue一覧
  - merged / closed を分離したPR数
  - Issueトラック（最初のPR作成 -> Issueクローズまでの時間）
4. 「統計情報ダウンロード」を実行し、JSONを保存する。

保存先:
- `reports/closed_issues_displayed_stats.json`

完了条件:
- 画面表示とダウンロードJSONの値が整合していること（重複カウントなし）。

## 4. 傾向分析レポートを作成

入力:
- `reports/closed_issues_displayed_stats.json`
- `reports/current_sprint_closed_issues_detail.json`

出力:
- `reports/closed_issues_trend_analysis_report.md`（日本語）

最低限含める内容:
- 全体傾向（PR数、修正行数、コメント数、リードタイム）
- 注意すべきIssueの抽出（理由つき）
- 想定原因と次アクション

## 5. レビュー資料（`docs/review`）を更新（準備フェーズ）

更新対象:
- `docs/review/01-feature-list.md`
- `docs/review/02-user-journey.md`
- `docs/review/03-er-diagram.md`
- `docs/review/04-class-diagram.md`
- `docs/review/05-sequence-diagrams.md`
- `docs/review/06-operation-manual.md`
- `docs/review/07-test-cases.md`

注意:
- 参照リンクは必ずリポジトリ相対パスを使う（`/Users/...` を書かない）。

補足:
- `docs/review/08-sprint-review-result.md` は準備フェーズでは更新しない。
- `08-sprint-review-result.md` はレビュー実施後（結果確定後）に更新する。

## 6. アプリ起動と主要導線の実機確認（Playwright MCP）

1. Backend起動（local profile）
2. Frontend起動
3. 主要画面を確認:
  - Participant: `/participant`
  - Admin: `/admin/auth` -> `/admin`
  - Operator: `/operator`
4. 主要スクリーンショットを保存:
  - `docs/images/manual/20260223-participant-main.png`
  - `docs/images/manual/20260223-admin-main.png`
  - `docs/images/manual/20260223-operator-checkin.png`
5. `docs/review/06-operation-manual.md` を最新化。

完了条件:
- 3ロールの主要導線とスクリーンショットが更新されていること。

## 7. 複雑度と単体テストカバレッジを計測

### 7-1. Complexity（lizard）

```bash
python3 -m pip install lizard
python3 -m lizard backend/src/main/java -l java > reports/backend_complexity_lizard.txt
python3 -m lizard frontend/src -l javascript -l typescript \
  -x "*/stories/*" -x "*.test.ts" -x "*.spec.ts" \
  > reports/frontend_complexity_lizard_app_only.txt
```

### 7-2. Backend coverage（JaCoCo）

```bash
(cd backend && ./gradlew test jacocoTestReport)
```

成果物:
- `backend/build/reports/jacoco/test/jacocoTestReport.xml`

### 7-3. Frontend coverage（Vitest）

必要に応じて追加:

```bash
(cd frontend && pnpm add -D @vitest/coverage-v8@3.2.4)
```

計測:

```bash
(cd frontend && pnpm test -- --coverage \
  --coverage.reporter=json-summary \
  --coverage.reporter=text \
  --coverage.include='src/**/*.{ts,vue}' \
  --coverage.exclude='src/stories/**' \
  --coverage.exclude='src/**/*.test.ts' \
  --coverage.exclude='src/**/*.spec.ts')
```

成果物:
- `frontend/coverage/coverage-summary.json`

### 7-4. 集約レポート作成

出力:
- `reports/complexity_coverage_metrics.json`
- `reports/complexity_coverage_report.md`

完了条件:
- Tech Leadコメントに「追加テスト箇所」「リファクタ対象」が追記されていること。

## 8. ロール別レビューコメントを作成

作成先:
- `review/comments/po_comments.md`
- `review/comments/designer_comments.md`
- `review/comments/qa_comments.md`
- `review/comments/techlead_comments.md`

実施順（推奨）:
1. PO（ビジネス価値）
2. Designer（UI/UX）
3. QA（機能不足/セキュリティ）
4. Tech Lead（設計/保守性/認証認可）

## 9. 最終チェックリスト

- [ ] `reports/current_sprint_closed_issues_detail.json` が最新
- [ ] `reports/closed_issues_displayed_stats.json` が最新
- [ ] `reports/closed_issues_trend_analysis_report.md` が最新（日本語）
- [ ] `docs/review/01-07` が更新済み（準備フェーズ）
- [ ] `docs/review/08-sprint-review-result.md` はレビュー実施後に更新する前提で未更新
- [ ] `docs/review/06-operation-manual.md` に最新スクショが反映済み
- [ ] `review/comments/*.md`（PO/Designer/QA/TechLead）が最新
- [ ] `reports/complexity_coverage_metrics.json` / `reports/complexity_coverage_report.md` が最新
- [ ] 絶対パス参照が残っていない（`/Users/...`）
- [ ] 実装Issueのclose時にPRリンクが記載されている（例外時は理由コメントあり）

## 10. 次スプリント向け改善（任意）

- 手動ステップを `Makefile` / `scripts/` に集約してワンコマンド化する
- カバレッジ閾値をCIゲート化する
- `review/comments` 生成テンプレートを用意してレビュー漏れを防ぐ
