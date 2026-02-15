# 機能一覧

## 1. 認証・セッション管理

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| 認証 | ゲストログイン | `POST /api/auth/guest` でアクセストークンと `guestId` を発行 |
| 認証 | APIアクセス制御 | 未認証は `401`、管理APIは `ROLE_ADMIN` のみ許可 |
| 認証 | 管理者トークン認証 | `Authorization: Bearer <admin-token>` で管理APIを実行 |

## 2. 予約管理（参加者）

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| 予約 | キーノート予約 | `POST /api/reservations/keynote`。予約完了時に参加登録完了 |
| 予約 | 通常セッション予約 | `POST /api/reservations/sessions/{sessionId}` |
| 予約 | 予約キャンセル | `DELETE /api/reservations/sessions/{sessionId}` |
| 予約 | 予約一覧取得 | `GET /api/reservations` |
| 予約 | セッション一覧取得 | `GET /api/reservations/sessions` |
| 予約 | マイページ取得 | `GET /api/reservations/mypage`（予約一覧 + 受付QR payload） |

## 3. 予約制約・ビジネスルール

- 同時間帯の予約は1件まで（同時刻予約時は既存予約を置換）
- 通常セッションは最大5件まで
- 予約/キャンセルは開始30分前を過ぎると不可
- 定員超過時は予約不可（`409`）
- 残席表示は3段階（`OPEN` / `FEW_LEFT` / `FULL`）

## 4. 運営機能

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| 管理 | セッション一覧（管理） | `GET /api/admin/sessions`（定員・予約数つき） |
| 管理 | セッション作成 | `POST /api/admin/sessions` |
| 管理 | セッション更新 | `PUT /api/admin/sessions/{sessionId}` |
| 管理 | CSV出力（予約） | `GET /api/admin/exports/reservations` |
| 管理 | CSV出力（チェックイン） | `GET /api/admin/exports/session-checkins` |

## 5. チェックイン

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| チェックイン | イベント受付 | `POST /api/checkins/event` |
| チェックイン | セッション受付 | `POST /api/checkins/sessions/{sessionId}` |
| チェックイン | 履歴表示 | `GET /api/checkins`（ログイン中ゲストの履歴のみ） |
| チェックイン | 重複判定 | 同一対象の再スキャン時は `duplicate=true` で返却 |
