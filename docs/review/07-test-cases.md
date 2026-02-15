# 自動テストのテストケース一覧（観点別）

## 1. 認証・認可

| 観点 | テストケース | ファイル |
| --- | --- | --- |
| ゲストログイン | トークン発行成功 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
| 未認証保護 | 保護APIで401 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` / `frontend/e2e/us01-guest-login.spec.ts` |
| ロール制御 | 管理APIをゲストトークンで実行すると403 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
| セッションTTL | 期限切れトークン拒否 | `backend/src/test/java/com/event/auth/GuestSessionServiceTest.java` |

## 2. 予約機能

| 観点 | テストケース | ファイル |
| --- | --- | --- |
| キーノート予約 | 予約成功で参加登録完了 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` / `frontend/e2e/us02-keynote-reservation.spec.ts` |
| 予約一覧 | 予約取得APIの応答整合性 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
| 同時間帯制約 | 同時刻予約時の置換動作 | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` |
| 上限制約 | 通常セッション最大5件制約 | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` / `frontend/e2e/us04-session-reservation-constraints.spec.ts` |
| 締切制約 | 開始30分前締切の検証 | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` / `backend/src/test/java/com/event/reservation/ReservationNowOverrideProfileTest.java` |
| 定員制約 | 定員超過時 `409` | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
| キャンセル | キャンセル成功/締切後不可 | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |

## 3. セッション管理（運営）

| 観点 | テストケース | ファイル |
| --- | --- | --- |
| 作成 | セッション新規作成 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` / `frontend/e2e/us08-admin-session-management.spec.ts` |
| 更新 | セッション更新と参加者一覧反映 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` / `frontend/e2e/us08-admin-session-management.spec.ts` |
| 更新制約 | 予約数未満の定員への変更拒否 | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` / `frontend/e2e/us08-admin-session-management.spec.ts` |
| 更新制約 | 既存予約と競合する時刻変更拒否 | `backend/src/test/java/com/event/reservation/ReservationServiceTest.java` |

## 4. チェックイン

| 観点 | テストケース | ファイル |
| --- | --- | --- |
| イベント受付 | 初回記録/重複判定 | `backend/src/test/java/com/event/checkin/CheckInServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
| セッション受付 | 初回記録/重複判定 | `backend/src/test/java/com/event/checkin/CheckInServiceTest.java` / `frontend/e2e/us07-checkin.spec.ts` |
| 妥当性 | QR形式不正・予約不一致の拒否 | `backend/src/test/java/com/event/checkin/CheckInServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
| 履歴 | 認証ユーザー単位で履歴取得 | `backend/src/test/java/com/event/checkin/CheckInServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |

## 5. CSVエクスポート

| 観点 | テストケース | ファイル |
| --- | --- | --- |
| 予約CSV | UTF-8ヘッダ・データ整合性 | `backend/src/test/java/com/event/export/CsvExportServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` / `frontend/e2e/us09-admin-csv-export.spec.ts` |
| チェックインCSV | 予約/チェックイン突合行の出力 | `backend/src/test/java/com/event/export/CsvExportServiceTest.java` / `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` / `frontend/e2e/us09-admin-csv-export.spec.ts` |
| 認可 | 未認証アクセスの拒否 | `frontend/e2e/us09-admin-csv-export.spec.ts` |

## 6. フロントエンドUI

| 観点 | テストケース | ファイル |
| --- | --- | --- |
| 初期表示 | 見出し・操作ボタン表示 | `frontend/src/App.test.ts` |
| セッション一覧 | 時刻/トラック/3段階ステータス表示 | `frontend/e2e/us03-session-list.spec.ts` |
| マイページ | 予約一覧・QR表示、未ログイン制御 | `frontend/e2e/us06-mypage-qr.spec.ts` |
| CORS | DELETE/PUTプリフライト許可 | `backend/src/test/java/com/event/security/GuestAuthenticationFlowTest.java` |
