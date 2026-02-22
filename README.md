# Event Reservation System

オンサイトテックイベント向け
チケット・セッション予約管理Webアプリ

---

## 🧭 Overview

本システムは以下を提供する：

- Google OAuthログイン
- キーノート（700席）予約
- 通常セッション（200席 × 15）予約
- 同時間帯重複予約防止
- 定員管理
- 当日QRチェックイン

---

## 🏗 Architecture

```
Frontend (Vue3 + Vite)
        ↓
Backend (Spring Boot REST API)
        ↓
PostgreSQL 17
```

---

## 📦 Repository Structure

```
/frontend      Vue3 + TypeScript
/backend       Spring Boot
README.md
```

---

## 🛠 Requirements

- Node.js 18+
- pnpm または npm
- JDK 21+（推奨: `openjdk@21`）
- PostgreSQL 17（ローカル起動）

---

## 🚀 ローカル開発環境構築

### 1️⃣ Clone

```bash
git clone <repository-url>
cd simple-event-reservation-system
```

---

### 2️⃣ PostgreSQL 17の初期設定（1回だけ）

`psql` で以下を実行：

```sql
CREATE ROLE event WITH LOGIN PASSWORD 'event';
CREATE DATABASE event OWNER event;
\c event
GRANT ALL PRIVILEGES ON DATABASE event TO event;
GRANT ALL ON SCHEMA public TO event;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO event;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO event;
```

接続確認：

```bash
psql -h localhost -p 5432 -U event -d event -c "SELECT version();"
```

---

### 3️⃣ バックエンド用ローカル設定を作成

`backend/src/main/resources/application-local.yml` を作成：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/event
    username: event
    password: event
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
app:
  auth:
    admin-operator-id: change-me-local-admin
    admin-password: change-me-local-admin-password
  reservation:
    # 任意: QAで現在時刻を固定したい場合のみ設定（ISO-8601 LocalDateTime）
    # now-override: 2026-01-01T10:01:00
```

管理者向けAPIを利用する場合は、`/admin/auth` で上記 `app.auth.admin-operator-id` / `app.auth.admin-password` を使ってログインしてください。

---

### 4️⃣ Run Backend（local profile）

```bash
cd backend
JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home ./gradlew bootRun --args='--spring.profiles.active=local'
```

Backend runs on:

```
http://localhost:8080
```

---

### 5️⃣ Run Frontend

```bash
cd frontend
pnpm install
pnpm dev --host 127.0.0.1 --port 5173
```

Frontend runs on:

```
http://localhost:5173
```

---

### 6️⃣ 起動確認

```bash
curl -i http://127.0.0.1:8080/api/health
curl -i http://127.0.0.1:5173
```

現在時刻オーバーライド確認（任意）:

1. `application-local.yml` に `app.reservation.now-override: 2026-01-01T10:01:00` を設定
2. バックエンドを `--spring.profiles.active=local` で再起動
3. セッション予約API（例: `POST /api/reservations/sessions/session-1`）で「開始30分前を過ぎたため予約できません。」を確認

---

## 🔐 Google OAuth Setup (Local)

1. Google Cloud ConsoleでOAuth Clientを作成
2. Authorized redirect URI に追加：

```
http://localhost:5173/login/callback
```

3. `.env` に設定：

### frontend/.env.local

```
VITE_GOOGLE_CLIENT_ID=xxxx
```

### backend/src/main/resources/application-local.yml

```yaml
google:
  client-id: xxxx
```

---

## 🗄 Database Migration

Flywayを使用。

```bash
cd backend
./gradlew flywayMigrate --args='--spring.profiles.active=local'
```

Migration files:

```
backend/src/main/resources/db/migration
```

---

## 🧪 Testing

### Backend

```bash
cd backend
./gradlew test
```

### Frontend

```bash
cd frontend
pnpm test
```

### Storybook

```bash
cd frontend
pnpm storybook
```

静的ビルド:

```bash
cd frontend
pnpm build-storybook
```

### Design Tokens (Style Dictionary)

トークン定義:
- `frontend/tokens/core/*.json`（プリミティブ）
- `frontend/tokens/semantic/*.json`（役割ベース）

トークンCSS生成:

```bash
cd frontend
pnpm tokens:build
```

生成先:
- `frontend/src/styles/tokens.css`

### E2E (Playwright)

```bash
cd frontend
pnpm exec playwright install
pnpm e2e
```

E2Eカバレッジ:
- `US-01` ゲストログイン受け入れ条件（導線・ゲスト利用開始・保護API 401・予約系API利用）
- `US-02` キーノート予約の主要導線（予約成功と参加登録完了表示）
- `US-04` 通常セッション予約導線と予約制約エラー表示（重複時間帯・最大5件）

GitHub Actions:
- E2Eは `.github/workflows/e2e-manual.yml` で管理
- 実行トリガーは `workflow_dispatch` のみ（手動実行専用）
- Storybookは `.github/workflows/docs-pages.yml` でGitHub Pagesへデプロイ（`/storybook/` 配下）

---

## 🔁 Environment Profiles

Spring profiles:

- local
- staging
- production

ローカル起動時：

```
--spring.profiles.active=local
```

---

## ⚠️ Notes

- `backend/src/main/resources/application-local.yml` はローカル専用（`.gitignore`対象）
- 定員超過を防ぐため、予約処理はトランザクション制御必須
- 同時間帯重複予約禁止ロジックはサーバ側で強制
- 残席数は数値表示しない（20未満で「残りわずか」）

---

## 📞 Support

不明点はIssueへ。
