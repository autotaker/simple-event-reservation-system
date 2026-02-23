# Tech Leadレビューコメント

- 観点: コード品質 / 保守性 / アーキテクチャ / 認証認可設計
- 対象コミット: `0b1b94f`
- 総合判定: **要改善（P1あり）**

## Findings（優先度順）

## [P1] イベント受付チェックインに参加資格整合チェックがない

- 該当:
  - `backend/src/main/java/com/event/checkin/CheckInService.java:41`
  - `backend/src/main/java/com/event/checkin/CheckInService.java:59`
- 問題:
  - `checkInEvent()` は QR を parse して重複判定のみ実施し、予約/参加登録の整合チェックをしていない。
  - 一方で `checkInSession()` は `reservations` と実予約を検証しているため、イベント受付だけ整合性ポリシーが弱い。
- 影響:
  - 受付データの信頼性低下、運用監査時の整合性崩壊リスク。
- 改善方向:
  - `checkInEvent()` にも参加資格判定（例: keynote予約 or 参加登録状態）を導入。
  - API結合テスト/E2Eで「資格なしは4xx」を固定化。

## [P2] 管理者認証が単一固定資格情報 + 失敗試行制御なし

- 該当:
  - `backend/src/main/java/com/event/auth/AdminSessionService.java:21`
  - `backend/src/main/java/com/event/auth/AdminSessionService.java:128`
  - `backend/src/main/java/com/event/auth/api/AuthController.java:36`
- 問題:
  - `admin-operator-id` / `admin-password` の単一固定資格情報を `equals` 比較で認証している。
  - 失敗回数・クールダウン・ロック等の試行制御がない。
- 影響:
  - ブルートフォース耐性が低く、認証境界のリスクが高い。
- 改善方向:
  - 認証失敗レート制限（IP / operatorId 単位）を導入。
  - 失敗イベント監査ログを実装。
  - 将来的に資格情報をDB化し、ハッシュ保管へ移行。

## [P2] 認証セッションがプロセス内メモリのみで、運用可用性が低い

- 該当:
  - `backend/src/main/java/com/event/auth/AdminSessionService.java:27`
  - `backend/src/main/java/com/event/auth/GuestSessionService.java:19`
- 問題:
  - admin/guest セッションがインメモリ `ConcurrentHashMap` 管理。
  - 再起動で全セッション消失、複数インスタンス構成で一貫性が保てない。
- 影響:
  - 本番運用時の可用性・スケーラビリティに制約。
- 改善方向:
  - セッションストア外部化（DB/Redis等）または署名付きトークン運用への移行。

## [P2] フロント状態管理が巨大な単一composableに集中し、変更影響範囲が広い

- 該当:
  - `frontend/src/composables/useReservationApp.ts:1`（全961行）
  - `frontend/src/composables/useReservationApp.ts:374`
  - `frontend/src/composables/useReservationApp.ts:507`
  - `frontend/src/composables/useReservationApp.ts:600`
  - `frontend/src/composables/useReservationApp.ts:782`
- 問題:
  - 参加者予約、管理者認証、管理CRUD、チェックイン、CSV出力が単一composableに混在。
  - ロール別変更時に副作用範囲が大きく、テスト戦略も粗くなりやすい。
- 影響:
  - 保守性低下、回帰リスク増、レビューコスト増。
- 改善方向:
  - `useParticipantFlow` / `useAdminFlow` / `useOperatorFlow` / `useAuth` へ分割。
  - APIクライアント層を分離し、UI state と通信処理の境界を明確化。

## [P3] APIベースURLがハードコードされ、環境切替の保守性が低い

- 該当:
  - `frontend/src/composables/useReservationApp.ts:107`
- 問題:
  - `API_BASE_URL = 'http://127.0.0.1:8080'` 固定。
- 影響:
  - 環境差分（stg/prod/preview）でビルド・配布時の設定ミスを誘発。
- 改善方向:
  - `import.meta.env` で環境注入（例: `VITE_API_BASE_URL`）へ移行。

## まとめ

- 認証認可とチェックイン整合性に高リスク箇所が残っているため、次スプリントで優先的に対応すべき。
- 特に `checkInEvent()` の整合性強化は、運用データ品質に直結するため最優先（P1）。

---

## Complexity / Coverage計測結果を踏まえた追記（2026-02-23）

- 参照:
  - `reports/complexity_coverage_report.md`
  - `reports/complexity_coverage_metrics.json`
- 全体所見:
  - Backendは `LINE 91.62%` / `BRANCH 71.76%` で一定水準。
  - Frontendは `lines 63.62%` / `branches 36.61%` / `functions 15.78%` で不足。
  - 特に `frontend/src/composables/useReservationApp.ts` が「複雑度集中 + 低カバレッジ」の単一障害点。

## 追加テストが必要な箇所（優先度順）

## [P1] `useReservationApp.ts` のユースケース分岐テスト不足

- 該当:
  - `frontend/src/composables/useReservationApp.ts:152`
  - `frontend/src/composables/useReservationApp.ts:374`
  - `frontend/src/composables/useReservationApp.ts:456`
  - `frontend/src/composables/useReservationApp.ts:782`
- 根拠:
  - ファイル単体で `lines 23.48% / functions 21.05%`。
  - 高CCN関数（`useReservationApp`, `reserveSession`, `downloadAdminCsv` など）が集中。
- 追加テスト:
  - `reserveSession` 正常系/重複予約/上限超過/期限切れ/401時トークン破棄。
  - `loadAdminSessions` の API失敗時エラーハンドリング分岐。
  - `loginAsAdmin` / `logoutAdmin` の成功・失敗・セッション切れ遷移。
  - `downloadAdminCsv` のレスポンス異常（非2xx/空ボディ）時挙動。

## [P2] 画面エントリとルーターの最低限回帰テスト不足

- 該当:
  - `frontend/src/main.ts:1`
  - `frontend/src/router/index.ts:1`
  - `frontend/src/views/AdminAuthView.vue:1`
  - `frontend/src/views/ParticipantView.vue:1`
- 根拠:
  - `main.ts` / `router/index.ts` が lines 0%。
  - `AdminAuthView.vue` は branches 12.5%、`ParticipantView.vue` は branches 41.17%。
- 追加テスト:
  - 主要ルート遷移（Participant/Admin/Operator）とガードのスナップショット/挙動固定。
  - Admin認証画面の入力バリデーション、失敗メッセージ、再試行導線。
  - Participant画面の分岐（予約済み/未予約/締切後）表示。

## [P2] Backendの境界条件テスト補強（複雑度高め関数）

- 該当:
  - `backend/src/main/java/com/event/export/CsvExportService.java:46`
  - `backend/src/main/java/com/event/auth/AdminSessionService.java:38`
  - `backend/src/main/java/com/event/checkin/CheckInQrPayloadParser.java:19`
- 根拠:
  - Backend全体カバレッジは高いが、CCN上位関数は仕様分岐が多い。
- 追加テスト:
  - CSV生成: 文字コード/改行/式インジェクション無害化の境界値。
  - AdminSession管理: セッション上限・期限切れevict・revoke後再利用不可。
  - QR parser: 不正フォーマット/欠損フィールド/型不整合。

## リファクタリングが必要な箇所（優先度順）

## [P1] `useReservationApp.ts` の責務分離

- 該当: `frontend/src/composables/useReservationApp.ts:1`（実質ロジック集中）
- 問題:
  - ロール別業務（participant/admin/operator）と認証・通信・UI状態が混在。
  - テスト粒度を小さくできず、関数カバレッジの伸びが悪い。
- 方針:
  - `useParticipantFlow` / `useAdminFlow` / `useOperatorFlow` / `useAuth` に分割。
  - APIアクセスを `frontend/src/api/*` に抽出し、副作用境界を固定。
  - 各composableは「状態 + 1責務」に限定。

## [P2] Backendサービスのユースケース単位分割

- 該当:
  - `backend/src/main/java/com/event/reservation/ReservationService.java:1`（NLOC 446）
  - `backend/src/main/java/com/event/export/CsvExportService.java:1`（Avg.NLOC 18.6）
- 問題:
  - ReservationServiceが集約しすぎで、機能変更時の影響面積が大きい。
- 方針:
  - `ReservationQueryService` / `ReservationCommandService` / `SessionAdminService` に分離。
  - CSV整形は `CsvRowMapper` 等へ抽出し、I/O層とドメイン判定を分離。

## [P2] 品質ゲートの導入

- 推奨基準（次スプリントのCI失敗条件候補）:
  - Frontend: lines >= 70%, branches >= 50%, functions >= 40%。
  - Backend: branch >= 75%（現状 71.76% のため重点改善対象を限定して引き上げ）。
  - Complexity: `CCN >= 10` 関数の新規追加時はテストケース追加を必須化。

---

## PBI-03 段階分割の開始条件（追記）

- 対象: `frontend/src/composables/useReservationApp.ts`
- 着手順:
  1. APIアクセス層の抽出（`frontend/src/api/*`）
  2. `useAuth` / `useParticipantFlow` / `useOperatorFlow` / `useAdminFlow` の順で責務分離
  3. 既存Viewの呼び出し先差し替え
- 依存関係:
  - PBI-01（チェックイン安全性）で追加する検証観点との整合
  - PBI-02（operator導線調整）でのUI変更と競合しない単位でPR分割
- 完了条件:
  - `useReservationApp.ts` からAPI通信と認証責務が分離されている
  - 既存のunit/API/E2E回帰が全件通過している
