# ユーザージャーニー（2026-02-23時点）

## 1. 参加者（ゲスト）

```mermaid
journey
  title 参加者の利用フロー（/participant）
  section ログイン
    ゲストでログインする: 5: 参加者
  section 予約
    タイムテーブルで候補を比較する: 5: 参加者
    キーノートを予約する: 5: 参加者
    通常セッションを予約・変更する: 4: 参加者
  section 当日利用
    マイページでQRを表示する: 5: 参加者
    受付でチェックインする: 4: 参加者
    セッション入場時にチェックインする: 4: 参加者
```

## 2. 管理者（運営）

```mermaid
journey
  title 管理者の利用フロー（/admin/auth -> /admin）
  section 認証
    /admin/auth でoperatorIdとパスワードを入力: 5: 運営
    管理者ログインして /admin へ遷移: 5: 運営
  section 運用
    セッション一覧を取得: 4: 運営
    セッション作成・更新: 4: 運営
    予約CSV・チェックインCSVを出力: 4: 運営
  section 終了
    ログアウトして当該トークンを失効: 5: 運営
```

## 3. オペレーター（現場運用）

```mermaid
journey
  title オペレーター運用フロー（/operator）
  section 受付
    イベント受付チェックインを実行: 5: 運営
  section セッション誘導
    セッション受付チェックインを実行: 5: 運営
  section 追跡
    チェックイン履歴を確認: 4: 運営
```

## 4. 主要タッチポイント

- 参加者UI: `/participant`
- 管理UI: `/admin/auth`, `/admin`
- 運用UI: `/operator`
- API: 認証、予約、管理、チェックイン、エクスポート
