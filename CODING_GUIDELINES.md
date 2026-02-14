# Coding Guidelines

Event Reservation System

---

# 1. 基本方針

- 可読性を最優先
- 明示的なコードを書く
- ビジネスルールは必ずサーバ側で強制
- フロントはUI、バックは整合性保証の責務を持つ
- 暗黙の仕様を作らない

---

# 2. 共通ルール

## 2.1 命名

- 英語で統一
- 省略禁止（`resv` など不可）
- booleanは `is` / `has` / `can` で始める

例:

```
isFull
canReserve
hasCheckedIn
```

---

## 2.2 Magic Number禁止

NG:

```
if (remaining < 20)
```

OK:

```
const LOW_SEAT_THRESHOLD = 20;
```

---

## 2.3 ログ

- 例外は必ずログ出力
- 個人情報はログに出さない
- INFOは業務イベントのみ

---

# 3. フロントエンド規約（Vue3 + TypeScript）

---

## 3.1 コンポーネント設計

- Composition API使用
- `<script setup>`形式
- 1コンポーネント1責務

NG:

- API呼び出し + 複雑ロジック + UI混在

OK:

- composableにロジック分離

---

## 3.2 状態管理

- セッション一覧などはstore管理可
- 予約操作は必ずAPI経由
- ローカルだけで整合性判断しない

---

## 3.3 API層

API呼び出しは `/api` ディレクトリに集約。

```
/src/api/session.ts
/src/api/reservation.ts
```

コンポーネント内で直接 `fetch` 書かない。

---

## 3.4 型定義

- `any` 禁止
- APIレスポンスは明示型
- backend DTOと一致させる

---

## 3.5 エラー処理

- APIエラーは共通ハンドラ
- 409 → 定員超過
- 400 → 重複予約
- 401 → 認証エラー

UIで必ずユーザーに意味ある表示を出す。

---

# 4. バックエンド規約（Spring Boot）

---

## 4.1 レイヤー構成

```
controller
service
repository
domain
```

- Controllerにビジネスロジックを書かない
- Repositoryを直接Controllerから呼ばない

---

## 4.2 トランザクション

予約処理は必ず：

```
@Transactional
```

- 定員チェックと予約登録は同一トランザクション

---

## 4.3 同時間帯チェック

- 必ずサーバ側で実施
- フロントチェックは補助のみ

---

## 4.4 例外設計

- ビジネス例外は専用クラス
- HTTPステータス明示

例:

```
SeatFullException → 409
TimeSlotConflictException → 400
```

---

## 4.5 DTO設計

- Entityを直接返さない
- ControllerはDTOのみ扱う
- 双方向参照禁止

---

## 4.6 バリデーション

- `@Valid` 使用
- 必須フィールドはアノテーション明示
- バリデーションエラーは統一フォーマットで返す

---

# 5. DB関連規約

---

## 5.1 マイグレーション

- 変更は必ずFlyway
- 既存migration修正禁止
- 追加のみ

---

## 5.2 インデックス

以下は必須：

- reservations(user_id)
- reservations(session_id)
- sessions(start_at)

---

## 5.3 排他制御

予約時：

- `SELECT ... FOR UPDATE`
- またはバージョン管理（楽観ロック）

---

# 6. Git規約

---

## 6.1 ブランチ

- main：本番
- develop：統合
- feature/\*：機能

---

## 6.2 コミットメッセージ

形式：

```
feat: add session reservation API
fix: prevent overbooking
refactor: extract reservation service
```

---

# 7. テスト規約

---

## 7.1 バックエンド

- Service層は単体テスト必須
- 予約ロジックは統合テスト必須
- 定員超過テスト必須

---

## 7.2 フロント

- APIモックテスト
- 主要画面の表示テスト

---

# 8. セキュリティ規約

- フロントで定員判定しない
- 管理APIはRole制御
- Googleトークン検証必須

---

# 9. 禁止事項

- 定員チェックをフロントのみで行う
- ControllerにSQLを書く
- ハードコードURL
- 例外を握りつぶす
- 本番データをローカルで使用

---

# 10. 完了条件（Definition of Done）

- テスト通過
- migration追加済み
- 型定義更新済み
- PRレビュー完了
- API仕様更新済み

---

以上。
