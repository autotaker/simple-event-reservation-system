# ER図（論理モデル）

MVP実装ではインメモリ保持（Map）ですが、業務的な関係をERとして整理すると以下です。

```mermaid
erDiagram
    GUEST ||--o{ RESERVATION : makes
    SESSION ||--o{ RESERVATION : has
    GUEST ||--o{ EVENT_CHECKIN : receives
    GUEST ||--o{ SESSION_CHECKIN : receives
    SESSION ||--o{ SESSION_CHECKIN : has

    GUEST {
      string guest_id PK
      string access_token
      datetime token_expires_at
    }

    SESSION {
      string session_id PK
      string title
      string track
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
```

## 制約

- `RESERVATION`: `(guest_id, session_id)` はユニーク
- 同時刻の `SESSION` に対して同一 `guest_id` の複数予約は禁止
- `SESSION.capacity` を超える予約は禁止
- チェックインは同一対象で重複時に `duplicate=true`
