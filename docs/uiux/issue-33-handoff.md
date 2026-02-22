# Issue #33 管理者認証モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- #33 の受け入れ条件に沿って、固定トークン依存から「operatorId + password ログイン + 短命トークン（30分固定） + 当該トークン失効」へ移行する。
- 会議決定（2026-02-22）に合わせて、`/admin/auth`（認証）と `/admin`（管理UI）の責務分離、および 401/403 導線分岐を実装前に固定する。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-33-admin-auth-flow.drawio`
- PNG(page1): `docs/images/uiux/issue-33-admin-auth-flow-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-33-admin-auth-flow-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-33-admin-auth-flow.png`

### HiFi（Storybook）

- Auth Layout: `frontend/src/stories/admin-auth-hifi/AdminAuthLayoutHiFi.vue`
- Auth Layout Stories: `frontend/src/stories/admin-auth-hifi/AdminAuthLayoutHiFi.stories.ts`
- Admin Forbidden Layout: `frontend/src/stories/admin-auth-hifi/AdminForbiddenLayoutHiFi.vue`
- Admin Forbidden Layout Stories: `frontend/src/stories/admin-auth-hifi/AdminForbiddenLayoutHiFi.stories.ts`
- Component Stories:
  - `frontend/src/stories/admin-auth-hifi/AdminAuthTopBarMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminLoginCardMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminTokenStatusPanelMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminSessionRevocationPanelMock.stories.ts`
  - `frontend/src/stories/admin-auth-hifi/AdminAuthDeniedPanelMock.stories.ts`（401）
  - `frontend/src/stories/admin-auth-hifi/AdminForbiddenPanelMock.stories.ts`（403）
- Storybook MDX: `frontend/src/stories/admin-auth-hifi/AdminAuthHiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## ユーザーフローチェックポイントとステータス

- CP1: 正規運用者が `operatorId + password` ログイン後に管理API利用可能 - Pass（認証済み状態を `/admin/auth` で定義）
- CP2: 401（未認証/期限切れ/失効/不正トークン）で token を即時破棄し `/admin/auth` へ再遷移 - Pass（401専用パネルと再ログイン CTA を定義）
- CP3: 403（権限不足）では `/admin` で管理UIを非表示にして拒否表示 - Pass（403専用レイアウトで表示境界を固定）
- CP4: 失効運用は `POST /api/auth/admin/logout` による当該トークン失効のみ - Pass（全失効/OTP/更新UIを除外）

## モック概要（実装引き渡し）

- `/admin/auth` は認証状態（未認証/認証中/認証済み/期限切れ/失効）を扱い、管理本体UIは含めない。
- `/admin` は認証済み前提の管理導線で、403時は管理UIを表示せず拒否パネルのみ表示する。
- 会議決定により、`Refresh token` 表示・更新ボタン・OTP入力・全失効UIは #33 スコープ外として除外する。

## レビューコメント対応（2026-02-22）

- 401内訳の判定: バックエンド `code` を利用し、`EXPIRED` / `REVOKED` を分岐表示。`UNAUTHORIZED`（未認証/不正トークン）は汎用401として再ログイン導線へ集約する。
- 未認証/不正トークンUI: 期限切れ/失効とは別に、`Invalid401` モックを追加した。
- `/admin/auth` の未認証表示: ログインカードのみ表示し、認証状態パネルと失効パネルは表示しない。
- 認証成功後の遷移: 自動遷移を必須にせず、`管理画面へ進む` CTA をモック上の基準にする。
- 403の戻り先: `/participant` 固定を前提にする。
- logout API失敗時UX: エラーメッセージを表示し、トークンは維持して `ログアウトして失効する` を再試行可能にする。401再返却時のみ即時破棄して再ログインへ遷移する。
- sessionStorage: キー名は `adminAccessToken` を使用し、破棄タイミングは「401受信時 + logout成功時」（403時は保持）を前提にする。

## 意思決定ログ（2026-02-22反映）

- 採用: `operatorId + password` / TTL30分固定 / 当該トークン失効 / `/admin/auth` と `/admin` 分離
- 非採用（別Issue候補）: refresh運用、全失効、OTP同時導入

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（会議決定反映済みのため、#33 実装PRへ進行可能）
