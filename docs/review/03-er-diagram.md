# ER図（論理モデル / 2026-02-23時点）

実装はインメモリ主体ですが、業務的な関係を論理モデルとして整理します。

```mermaid
erDiagram
    GUEST ||--o{ RESERVATION : makes
    SESSION ||--o{ RESERVATION : has
    GUEST ||--o{ EVENT_CHECKIN : receives
    GUEST ||--o{ SESSION_CHECKIN : receives
    SESSION ||--o{ SESSION_CHECKIN : has
    ADMIN_OPERATOR ||--o{ ADMIN_SESSION_TOKEN : issues

    GUEST {
      string guest_id PK
      datetime token_expires_at
    }

    SESSION {
      string session_id PK
      string title
      string track
      int display_order
      string start_time
      int capacity
    }

    RESERVATION {
      string guest_id FK
      string session_id FK
      datetime reserved_at
    }

    EVENT_CHECKIN {
      string guest_id FK
      datetime checked_in_at
      bool duplicate
    }

    SESSION_CHECKIN {
      string guest_id FK
      string session_id FK
      datetime checked_in_at
      bool duplicate
    }

    ADMIN_OPERATOR {
      string operator_id PK
      string password_hash
      bool enabled
    }

    ADMIN_SESSION_TOKEN {
      string token_id PK
      string operator_id FK
      datetime issued_at
      datetime expires_at
      datetime revoked_at
    }
```

## 制約

- `RESERVATION`: `(guest_id, session_id)` はユニーク
- 同時刻 `SESSION` に対して同一 `guest_id` の複数予約は禁止
- `SESSION.capacity` を超える予約は禁止
- チェックインは同一対象で重複時に `duplicate=true`
- 管理者APIは有効な `ADMIN_SESSION_TOKEN` が必要
