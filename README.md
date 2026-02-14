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
PostgreSQL
```

---

## 📦 Repository Structure

```
/frontend      Vue3 + TypeScript
/backend       Spring Boot
/docker-compose.yml
README.md
```

---

## 🛠 Requirements

- Node.js 18+
- pnpm または npm
- JDK 17+
- Docker
- Docker Compose

---

## 🚀 Getting Started

### 1️⃣ Clone

```bash
git clone <repository-url>
cd event-app
```

---

### 2️⃣ Start Database

```bash
docker compose up -d
```

PostgreSQL:

- Host: localhost
- Port: 5432
- DB: event
- User: event
- Password: event

---

### 3️⃣ Run Backend

```bash
cd backend
./gradlew bootRun
```

Backend runs on:

```
http://localhost:8080
```

---

### 4️⃣ Run Frontend

```bash
cd frontend
pnpm install
pnpm dev
```

Frontend runs on:

```
http://localhost:5173
```

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

### backend/application-local.yml

```
google:
  client-id: xxxx
```

---

## 🗄 Database Migration

Flywayを使用。

```bash
./gradlew flywayMigrate
```

Migration files:

```
backend/src/main/resources/db/migration
```

---

## 🧪 Seed Data

ローカル起動時に以下を自動生成：

- キーノート 1件
- 通常セッション 15件
- 5時間帯構成

---

## 🧪 Testing

### Backend

```bash
./gradlew test
```

### Frontend

```bash
pnpm test
```

---

## 📋 Development Rules

- mainブランチ直接コミット禁止
- PR必須
- DB変更は必ずFlyway migration追加
- API変更時はOpenAPI定義更新

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

## 🧱 API Base Path

```
/api/*
```

例：

```
GET /api/sessions
POST /api/reservations
```

---

## 📲 QR Check-In Flow

- ユーザーはマイページでQR表示
- 管理画面でスキャン
- 状態遷移: reserved → checked_in

---

## 🧭 Roadmap

- 管理画面UI改善
- ログ可視化
- 次回イベント対応検討

---

## 🧹 Stop Database

```bash
docker compose down
```

---

## ⚠️ Notes

- 定員超過を防ぐため、予約処理はトランザクション制御必須
- 同時間帯重複予約禁止ロジックはサーバ側で強制
- 残席数は数値表示しない（20未満で「残りわずか」）

---

## 📞 Support

不明点はIssueへ。
