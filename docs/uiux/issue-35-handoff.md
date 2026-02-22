# Issue #35 予約情報可読化モック合意メモ

## Phase handled

- pre-PR mock

## 目的

- `sessionId` 依存表示を避け、参加者がマイページ予約情報を「タイトル > 時刻・トラック」で理解できる表示モデルを実装前に固定する。
- 既存の予約キャンセル導線を維持したまま、読み取り負荷を下げる。

## モック成果物

### LoFi（draw.io ワイヤーフレーム）

- draw.io: `docs/uiux/issue-35-readable-mypage.drawio`
- PNG(page1): `docs/images/uiux/issue-35-readable-mypage-p1.png`（Mobile 390x844）
- PNG(page2): `docs/images/uiux/issue-35-readable-mypage-p2.png`（Tablet 834x1112）
- PNG(merged): `docs/images/uiux/issue-35-readable-mypage.png`

### HiFi（Storybook）

- Layout: `frontend/src/stories/issue-35-hifi/Issue35ParticipantLayoutHiFi.vue`
- Layout Stories: `frontend/src/stories/issue-35-hifi/Issue35ParticipantLayoutHiFi.stories.ts`
- Component Stories:
  - `frontend/src/stories/issue-35-hifi/Issue35TopBarMock.stories.ts`
  - `frontend/src/stories/issue-35-hifi/Issue35SessionCardMock.stories.ts`
  - `frontend/src/stories/issue-35-hifi/Issue35ReservationPanelMock.stories.ts`
  - `frontend/src/stories/issue-35-hifi/Issue35MyPageReadablePanelMock.stories.ts`
- Storybook MDX: `frontend/src/stories/issue-35-hifi/Issue35HiFiCatalog.mdx`

## デバイス想定

- Primary: 参加者スマートフォン（390x844 前後）
- Secondary: 受付補助タブレット（834x1112 前後）
- Tertiary: 来場前PC確認（1280幅）

## ユーザーフローチェックポイントとステータス

- CP1: `sessionId` 生表示をUI主表示から除外できる - Pass（MyPage readable panel で表示優先順位を固定）
- CP2: マイページ/予約一覧の可読性を向上できる - Pass（タイトル主表示 + メタ1行 + 補助トーン）
- CP3: 予約キャンセル導線を維持する - Pass（Reservation panel mock で `キャンセル` CTA を維持）

## モック概要（実装引き渡し）

- マイページ予約リストは1件2行以内を基準とし、タイトルを主表示、時刻・トラックを副表示にする。
- データ欠損時は raw ID を出さず、`不明なセッション` と更新誘導文言を表示する。
- QR導線は主目的として維持し、予約情報は補助情報として視認可能な密度に抑える。

## 仕様固定（実装前合意）

### 1. フォールバック発動条件

- 発動単位は **行単位** とする。
- 予約配列に正常データと欠損データが混在する場合は、欠損行のみ `不明なセッション` を表示する。
- 全件欠損の場合のみ、リスト全行がフォールバック表示になる。

### 2. loading / error 時の表示方針

- `loading` 時: 直前に表示していた予約リストを維持し、ステータス文言のみ上書き表示する。
- `error` 時: 直前に表示していた予約リストを維持し、エラーステータスを併記する。
- 取得失敗を理由に、既存の表示済み予約リストは即時クリアしない。

### 3. 時刻・トラック表示フォーマット

- 正規形は `HH:mm | Track X` とする（例: `11:30 | Track B`）。
- 区切り文字は常に ` | ` を使用する。
- トラック不明時は `Track 未設定` を表示する。

### 4. 境界ケース

- 予約0件時: `予約はありません。` を表示する。
- 多件時（モバイル）: 最大5件想定。リスト領域は縦スクロールで表示を維持し、QR導線を押し下げない。
- 多件時（タブレット/PC）: 2カラム構成を維持し、リストのみスクロール可能とする。

## UX findings

- なし（pre-PR mock フェーズのため、実装後に post-merge UX test で検証）

## Overall recommendation

- proceed（Issue #35 実装PRへ進行可能）
