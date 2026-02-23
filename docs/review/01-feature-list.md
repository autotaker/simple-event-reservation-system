# 機能一覧（2026-02-23時点）

## 1. 認証・セッション管理

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| 認証 | ゲストログイン | `POST /api/auth/guest` でアクセストークンと `guestId` を発行 |
| 認証 | 管理者ログイン | `POST /api/auth/admin` で短命トークンを発行（`operatorId` + `password`） |
| 認証 | 管理者ログアウト | `POST /api/auth/admin/logout` で当該トークンを失効 |
| 認可 | APIアクセス制御 | 未認証は `401`、権限不足は `403` |
| 認可 | 管理導線ガード | `/admin` は管理セッションなし時に `/admin/auth` へリダイレクト |

## 2. 参加者機能（`/participant`）

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| 予約 | キーノート予約 | `POST /api/reservations/keynote` |
| 予約 | 通常セッション予約 | `POST /api/reservations/sessions/{sessionId}` |
| 予約 | 予約キャンセル | `DELETE /api/reservations/sessions/{sessionId}` |
| 参照 | セッション一覧取得 | `GET /api/reservations/sessions`（`displayOrder` を含む） |
| 参照 | 予約一覧取得 | `GET /api/reservations` |
| 参照 | マイページ取得 | `GET /api/reservations/mypage`（予約一覧 + 受付QR payload） |
| UI | タイムテーブル表示 | セッションを時刻・トラック軸で表示 |
| UI | 予約情報可読化 | 予約情報を時刻・トラック・タイトル中心で表示 |

## 3. 予約制約・ビジネスルール

- 同時間帯の予約は1件まで（同時刻予約時は既存予約を置換）
- 通常セッションは最大5件まで
- 予約/キャンセルは開始30分前を過ぎると不可
- 定員超過時は予約不可（`409`）
- 残席表示は3段階（`OPEN` / `FEW_LEFT` / `FULL`）

## 4. 管理機能（`/admin`, `/operator`）

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| 管理 | セッション一覧（管理） | `GET /api/admin/sessions`（定員・予約数つき） |
| 管理 | セッション作成 | `POST /api/admin/sessions` |
| 管理 | セッション更新 | `PUT /api/admin/sessions/{sessionId}` |
| 管理 | CSV出力（予約） | `GET /api/admin/exports/reservations` |
| 管理 | CSV出力（チェックイン） | `GET /api/admin/exports/session-checkins` |
| 運用 | オペレーター画面 | `/operator` でチェックイン運用UIを提供 |

## 5. チェックイン

| 区分 | 機能 | 概要 |
| --- | --- | --- |
| チェックイン | イベント受付 | `POST /api/checkins/event` |
| チェックイン | セッション受付 | `POST /api/checkins/sessions/{sessionId}` |
| チェックイン | 履歴表示 | `GET /api/checkins`（ログイン中ゲストの履歴のみ） |
| チェックイン | 権限厳格化 | 管理者は全件、ゲストは本人一致時のみ書き込み可 |
| チェックイン | 重複判定 | 同一対象の再スキャン時は `duplicate=true` |

## 6. セキュリティ関連の現行方針

- 管理者トークンの永続保存を廃止（フロント側で永続化しない）
- `/admin` 直接アクセス時はセッション確認を行い、未認証時は認証導線へ誘導
- 失効済み/期限切れトークン時は再認証を要求
