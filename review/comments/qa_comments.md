# QAレビューコメント（機能不足・セキュリティ観点）

- レビュー基準: `QA_GUIDELINE.md`
- 実施日時: 2026-02-23
- 対象コミット: `0b1b94f`
- 対象環境: local (`127.0.0.1:8080`, `127.0.0.1:5173`)
- 総合判定: **Fail（P1あり）**

## 指摘事項（優先度順）

## [P1] イベント受付チェックインが「予約なし」でも成立してしまう

- 事象:
  - `POST /api/checkins/event` が、`reservations=` の空QR payloadでも `200` でチェックイン成功する。
  - セッション受付は予約整合チェックがある一方、イベント受付は予約有無の整合チェックがない。
- 再現手順:
  1. `POST /api/auth/guest` でトークン取得
  2. `qrCodePayload=event-reservation://checkin?guestId=<自分>&reservations=` を `POST /api/checkins/event` に送信
- 期待結果:
  - 予約・参加登録要件に未達なら `4xx`（拒否）になること
- 実結果:
  - `200` でイベント受付チェックインが記録される
- 影響範囲:
  - 参加登録フローを経ていないゲストでも受付成立しうるため、運用データの正確性と受付統制に影響
- 根拠:
  - `backend/src/main/java/com/event/checkin/CheckInService.java:41` 以降の `checkInEvent()` に予約整合チェックが存在しない
  - `checkInSession()` には `reservations` と実予約の整合チェックが存在（`backend/src/main/java/com/event/checkin/CheckInService.java:59-64`）
- 改善提案:
  1. `checkInEvent()` でも最低限の参加資格チェック（例: keynote予約または参加登録フラグ）を実装
  2. 同観点のAPI結合テスト/E2Eを追加して回帰防止

## [P2] 管理者認証APIに試行回数制御（レート制限）の確認ができない

- 事象:
  - `POST /api/auth/admin` へ連続で誤パスワードを送っても、同一応答 (`401`) が即時返却され続ける。
- 再現手順:
  1. 誤パスワードで `POST /api/auth/admin` を短時間に複数回実行
  2. すべて即時 `401` で返ることを確認
- 期待結果:
  - 一定回数以上の失敗で待機時間増加、ロック、または監査イベント強化などの防御があること
- 実結果:
  - 試行回数に応じた制御は確認できない
- 影響範囲:
  - 認証試行に対する耐性が不足すると、総当たり攻撃リスクが上がる
- 改善提案:
  1. `operatorId` 単位またはIP単位での試行制御を導入
  2. 認証失敗イベントを監査ログ化
  3. 関連するテストケースを追加

## 参考（今回の確認で問題なし）

- `GET /api/admin/sessions`
  - 未認証: `401`
  - ゲストトークン: `403`
  - 管理者トークン: `200`
- `POST /api/auth/admin/logout` 後の同トークン再利用
  - `GET /api/admin/sessions` が `401` となり、失効動作を確認
- チェックイン書き込み権限
  - ゲストが他人 `guestId` で event/session チェックインを試すと `403`

## QA判定

- **Fail**
- 主因: P1（イベント受付チェックインの資格チェック不足）

---

## PBI-01 受け入れテスト観点（追記）

- 対象PBI: イベント受付チェックイン資格判定の必須化
- 判定条件:
  - 資格なし `event` チェックインが `4xx`
  - 資格あり `event` チェックインが `200`
  - 回帰E2Eが安定通過

### API結合テスト観点

- ケース1: `reservations=`（空）で `POST /api/checkins/event` -> `4xx`
- ケース2: 不正payloadで `POST /api/checkins/event` -> `4xx`
- ケース3: 資格ありpayloadで `POST /api/checkins/event` -> `200`
- ケース4: 同一payload再送で重複防止が維持される

### UI E2E観点

- オペレーター導線で資格なしQRを入力した場合、チェックイン成功にならない
- 資格ありQRのみ成功トースト/履歴反映になる
- 失敗時のメッセージが次アクションを判断できる文言になっている
