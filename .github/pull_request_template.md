## 概要
- このPRで何を解決するかを記載してください。

## 変更内容
- 
- 
- 

## 確認手順
1. 
2. 
3. 

## テスト
- [ ] `frontend` のテストを実行した
- [ ] `backend` のテストを実行した
- [ ] ローカルで起動確認した

実行コマンド（必要に応じて）:
```bash
cd frontend && pnpm lint && pnpm typecheck && pnpm test && pnpm build
cd backend && ./gradlew check test bootJar
```

## 仕様準拠チェック（UI/CSS変更がある場合）
- [ ] 契約クラス（例: `ui-shell` / `ui-button` / `ui-field` / `ui-table` / `ui-status`）を使用している
- [ ] 状態バリエーション（`loading` / `success` / `error` / `disabled`）を要件どおり網羅している
- [ ] 禁止事項（直値カラー、画面ごとの独自disabled表現、エラー表示位置の不統一）に抵触していない

## 影響範囲
- [ ] Frontend
- [ ] Backend
- [ ] Database（Migrationあり）
- [ ] CI/CD
- [ ] ドキュメント

## 破壊的変更
- [ ] なし
- [ ] あり（内容を記載）

## 関連Issue
- Closes #
- Related #

## レビューポイント
- レビュアーに重点的に見てほしい点を記載してください。

## 補足
- スクリーンショットや補足事項があれば記載してください。
