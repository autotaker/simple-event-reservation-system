# Closed Issues 傾向分析レポート

- 作成日: 2026-02-23
- 分析元データ:
  - `reports/closed_issues_displayed_stats.json`
  - `reports/current_sprint_closed_issues_detail.json`

## 1. サマリー

- Issue数: 15
- ユニーク修正PR数: 32
- 総変更行数（add+del）: 18,060
- Issueコメント総数: 54

### サイクルタイム（最初の修正PR作成 -> Issue close）

- 算出対象: 14件（1件は修正PR時刻を取得不可）
- 最小: 10分
- 中央値: 16時間39分
- 75パーセンタイル: 1日12時間
- 最大: 4日17時間44分
- 平均: 約25時間38分

## 2. 全体傾向

1. サイクルタイムは二極化している
- 実装中心のIssueは「数分〜数時間」でclose
- 親Issue/調整型/再作業が絡むIssueは「1.5日〜5日」に伸びる

2. PR churn（作り直し・差し替え）が複数Issueで発生
- merged PRとclosed未merge PRが同一Issue内に混在

3. トレーサビリティにばらつきがある
- 修正PRが明確に紐づかないまま、検証コメント起点でcloseされるケースがある

## 3. 注意すべきIssue（優先順）

## #30 `[Post-MVP][US-12] 参加者画面と管理画面の導線を分離する`

- サイクルタイム: 4日17時間44分（最長）
- 修正PR: merged 0 / closed未merge 1
- 関連PR数: 12
- 注目理由:
  - 実質的にエピック（親Issue）で、子Issue完了待ちでcloseされる運用
  - 複数文脈の関連PRが集まり、複雑度が高く見える
- リスク:
  - 実装Issueと同列のKPIで比較すると、開発速度の指標を歪める

## #41 `[Post-MVP][US-12-3] 新導線に合わせてE2Eを更新する`

- 修正PR数: 0
- 変更行数: 0
- 注目理由:
  - mainでの再検証コメントを主因としてcloseされている
- リスク:
  - 「どのコード変更で満たしたか」の追跡性が弱い

## #29 `[Post-MVP][US-11] チェックイン書き込み権限を厳格化する`

- サイクルタイム: 2日21時間55分
- 修正PR数: 2（いずれもmerged）
- 変更行数: 1,164
- 注目理由:
  - 初回マージ後のUXレビューFailにより追加PRと再テストが発生
- リスク:
  - マージ後手戻りループがリードタイムを押し上げる

## #40 `[Post-MVP][US-12-2] 管理導線 (/admin) と直接アクセス制御を実装する`

- サイクルタイム: 4時間（短いがchurnが高い）
- 修正PR: merged 3 / closed未merge 3
- 関連PR数: 10
- 変更行数: 2,901
- 注目理由:
  - 短時間にモック/実装の差し替えが複数発生
- リスク:
  - レビュー・調整負荷が増え、指標のノイズが増える

## #34 `[Post-MVP][US-16] クライアントでの機密トークン永続保存を廃止する`

- サイクルタイム: 2日0時間33分
- 修正PR: merged 1 / closed未merge 1
- 注目理由:
  - #64をcloseして#78で再提出、かつ#33依存あり
- リスク:
  - 依存待ち・再提出が実装遅延として見えてしまう

## 4. 根本要因パターン

1. 異なるIssueタイプを同一KPIで計測
- `epic` / `implementation` / `verification` が混在

2. close運用のばらつき
- `Closes` と `Related`、コメント起点closeが混在

3. 品質検証タイミング
- QA/UXのFailがマージ後に顕在化し、追加入りPRを誘発

## 5. 改善提案

1. KPIをIssueタイプ別に分離する
- `epic` / `implementation` / `verification`

2. closeルールを標準化する
- 実装Issueは修正PR紐付け必須（例外時はテンプレ必須）

3. PRリンク運用を明確化する
- 主対象は `Closes #x` を1件明記、補足は `Related #x` に限定

4. QA/UXを前倒しする
- 最低限のマージ前ゲートを設け、マージ後の修正ループを抑える

## 6. 付録（外れ値データ）

- #30: cycle 4日17時間44分, merged 0, closed未merge 1, related 12, lines 388
- #29: cycle 2日21時間55分, merged 2, closed未merge 0, related 2, lines 1,164
- #34: cycle 2日0時間33分, merged 1, closed未merge 1, related 2, lines 175
- #33: cycle 1日12時間38分, merged 3, closed未merge 0, related 4, lines 2,388
- #41: 修正PR時刻なし, merged 0, closed未merge 0, related 1, lines 0
