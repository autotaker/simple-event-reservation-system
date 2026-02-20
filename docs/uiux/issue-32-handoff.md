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
- FEW_LEFT: 残りわずか。セルCTAは活性（`予約する`）で注意トーンを適用（残席 `1..20`）。
- FULL: 満席。セルCTAは非活性（`満席`）。
- 予約済み: ユーザー予約済み。セルCTAは非活性（`予約済み`）。

## 仕様確定（開発着手前）

1. `FEW_LEFT` 閾値

- `FEW_LEFT` は残席 `1..20` を採用する（現行実装 `FEW_SEATS_THRESHOLD = 20` に合わせる）。

2. ローディング時の操作ロック範囲

- 予約送信中は画面単位ロックとする（タイムテーブルと予約パネルを操作不可）。
- 理由: 同時刻自動置換を含む状態更新で、部分操作を許すと視覚不整合が発生しやすいため。

3. 同時刻予約の置換時UI

- 成功時は「新規予約セルを予約済み化」だけでなく、「置換元セルを即時に非予約状態へ戻す」まで1トランザクションとして反映する。
- 成功フィードバック文言は置換を明示する（例: `11:00 Track A に変更しました（Track B を解除）`）。

4. エラー表示位置

- エラーは画面上部の共通フィードバックエリアに表示する（セル下部ではなく共通配置）。

5. Keynoteの扱い

- Keynote は通常セッションと同様にタイムテーブルに含める。
- 予約可否はAPIの在庫/制約に従い、予約不可なら `FULL` もしくは制約エラー導線で扱う。

6. マトリクス生成ルール（欠損セル/トラック順）

- トラック順は `displayOrder` 昇順を正とし、未設定時はトラック名昇順でフォールバックする。
- セッション欠損セルは空欄ではなく `セッションなし`（非活性セル）を表示する。

## 操作仕様

- 活性条件
  - OPEN / FEW_LEFT のみ予約実行可。
  - FULL / 予約済み / セッションなし / loading時はセルCTAを非活性。
- 反映順序
  1. 予約押下後、画面全体をloading相当でロック
  2. 成功時、対象セルを`予約済み`へ更新し、同時刻の置換元セルを非予約状態へ戻す
  3. 予約一覧パネルを更新
  4. 成功フィードバック（画面上部共通）を表示
- エラー時挙動
  - セル状態は送信前状態に戻す
  - 予約一覧は更新しない
  - エラーフィードバックを画面上部共通エリアへ表示（再試行導線を明示）

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

- エラー文言の文体（丁寧語/常体）は全体ガイドラインに合わせて最終調整。

## 決定ログ

- 2026-02-20: セッション一覧の主表現をカード一覧からタイムテーブルへ変更。
- 2026-02-20: 状態優先順位を `予約済み > FULL > FEW_LEFT > OPEN` として整理。
- 2026-02-20: Mobileでは横スクロール許容、可読性維持を優先する方針で合意。
- 2026-02-20: `FEW_LEFT` 閾値を `1..20`、ローディング中の画面単位ロック、同時刻置換の同時反映を確定。
- 2026-02-20: エラー表示を画面上部共通エリアへ統一、Keynoteをタイムテーブル対象として扱う方針を確定。
