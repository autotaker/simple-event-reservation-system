# Issue #32 セッションタイムテーブル UI モック引き渡しメモ

## Phase handled

- pre-PR mock

## 目的

- 受け入れ条件（CP1-CP3）を満たすために、セッション一覧を「時間×トラック」の比較前提で再設計する。
- 予約判断を短縮するため、セル単位で「状態識別」と「予約操作」を同時に扱う導線を固定する。

## モック成果物

### LoFi（draw.io）

- draw.io: `docs/uiux/issue-32-session-timetable.drawio`
- PNG(page1): `docs/images/uiux/issue-32-session-timetable-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-32-session-timetable-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-32-session-timetable.png`

### HiFi（Storybook）

- Layout: `frontend/src/stories/participant-hifi/ParticipantTimetableLayoutHiFi.vue`
- Layout Stories: `frontend/src/stories/participant-hifi/ParticipantTimetableLayoutHiFi.stories.ts`
- Timetable Mock: `frontend/src/stories/participant-hifi/ParticipantSessionTimetableMock.vue`
- Timetable Stories: `frontend/src/stories/participant-hifi/ParticipantSessionTimetableMock.stories.ts`
- Storybook MDX: `frontend/src/stories/participant-hifi/ParticipantTimetableHiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## CP対応表（受け入れ条件）

- CP1: 時間×トラックで表示される
  - 対応: LoFi p1/p2 のテーブル領域 + HiFi Timetable Layout
- CP2: 各枠で予約操作できる
  - 対応: OPEN / FEW_LEFT セルに `予約する` CTA を配置
- CP3: 満席/予約済み状態がセル内で識別できる
  - 対応: FULL / 予約済みトーン + disabledボタン +ラベル表示

## 状態定義

- OPEN: 空席あり。セルCTAは活性（`予約する`）。
- FEW_LEFT: 残りわずか。セルCTAは活性（`予約する`）で注意トーンを適用。
- FULL: 満席。セルCTAは非活性（`満席`）。
- 予約済み: ユーザー予約済み。セルCTAは非活性（`予約済み`）。

## 操作仕様

- 活性条件
  - OPEN / FEW_LEFT のみ予約実行可。
  - FULL / 予約済み / loading時はセルCTAを非活性。
- 反映順序
  1. 予約押下後、対象セルをloading相当でロック
  2. 成功時、対象セルを`予約済み`へ更新
  3. 予約一覧パネルを更新
  4. 成功フィードバック（inline）を表示
- エラー時挙動
  - セル状態は送信前状態に戻す
  - 予約一覧は更新しない
  - エラーフィードバックをセル下部へ表示（再試行導線を明示）

## レスポンシブ制約

- Mobile（390幅）
  - タイムテーブルは横スクロール前提（トラック列は折り返さない）
  - Time列は固定幅で比較軸を維持
- Tablet（834幅）
  - タイムテーブル + 補助パネル（予約一覧/QR）の2カラム
- トラック増減時
  - 4列超は横スクロールで対応し、セル最小幅を維持
  - 列幅圧縮で文字欠落を起こさないことを優先

## ユーザーフローチェックポイントとステータス

- CP1（比較）: 時間×トラックで比較できる - Pass
- CP2（判断→予約）: 各セルから予約操作へ進める - Pass
- CP3（状態反映）: 満席/予約済みをセル内識別できる - Pass

## 未確定事項

- `FEW_LEFT` の閾値（残席数）はバックエンド仕様の最終確定待ち。
- エラー文言の文体（丁寧語/常体）は全体ガイドラインに合わせて最終調整。

## 決定ログ

- 2026-02-20: セッション一覧の主表現をカード一覧からタイムテーブルへ変更。
- 2026-02-20: 状態優先順位を `予約済み > FULL > FEW_LEFT > OPEN` として整理。
- 2026-02-20: Mobileでは横スクロール許容、可読性維持を優先する方針で合意。
