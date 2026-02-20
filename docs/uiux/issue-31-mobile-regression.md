# Issue #31 モバイル回帰確認メモ

## 対象

- participant / admin / operator の実コンポーネント構成
- 共通CSS基盤クラス（`ui-shell`, `ui-button`, `ui-field`, `ui-table`, `ui-status`）

## 確認手段

- Storybook: `frontend/src/stories/issue-31-production/Issue31ProductionCatalog.mdx`
- Viewport: 390x844 / 834x1112 / 1280x800

## 確認結果

- [x] 390x844 で主要操作が崩れない
- [x] 834x1112 で導線ごとの主要情報が視認可能
- [x] 1280x800 で余白・階層・状態表示が一貫
- [x] loading / success / error / disabled の識別が可能

## 補足

- 本メモは Issue #31 フォローアップ実装時点の確認結果。
