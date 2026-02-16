## Skills
A skill is a set of local instructions to follow that is stored in a `SKILL.md` file. Below is the list of skills that can be used. Each entry includes a name, description, and file path so you can open the source for full instructions when using a specific skill.
### Available skills
- skill-creator: Guide for creating effective skills. This skill should be used when users want to create a new skill (or update an existing skill) that extends Codex's capabilities with specialized knowledge, workflows, or tool integrations. (file: /Users/autotaker/.codex/skills/.system/skill-creator/SKILL.md)
- skill-installer: Install Codex skills into $CODEX_HOME/skills from a curated list or a GitHub repo path. Use when a user asks to list installable skills, install a curated skill, or install a skill from another repo (including private repos). (file: /Users/autotaker/.codex/skills/.system/skill-installer/SKILL.md)
- issue-task-completer: Complete work for a specified GitHub Issue number, including scope確認, 実装, 検証, PR連携, Issue更新までを一気通貫で進める。 (file: /Users/autotaker/.codex/skills/issue-task-completer/SKILL.md)
### How to use skills
- Discovery: The list above is the skills available in this session (name + description + file path). Skill bodies live on disk at the listed paths.
- Trigger rules: If the user names a skill (with `$SkillName` or plain text) OR the task clearly matches a skill's description shown above, you must use that skill for that turn. Multiple mentions mean use them all. Do not carry skills across turns unless re-mentioned.
- Missing/blocked: If a named skill isn't in the list or the path can't be read, say so briefly and continue with the best fallback.
- How to use a skill (progressive disclosure):
  1) After deciding to use a skill, open its `SKILL.md`. Read only enough to follow the workflow.
  2) When `SKILL.md` references relative paths (e.g., `scripts/foo.py`), resolve them relative to the skill directory listed above first, and only consider other paths if needed.
  3) If `SKILL.md` points to extra folders such as `references/`, load only the specific files needed for the request; don't bulk-load everything.
  4) If `scripts/` exist, prefer running or patching them instead of retyping large code blocks.
  5) If `assets/` or templates exist, reuse them instead of recreating from scratch.
- Coordination and sequencing:
  - If multiple skills apply, choose the minimal set that covers the request and state the order you'll use them.
  - Announce which skill(s) you're using and why (one short line). If you skip an obvious skill, say why.
- Context hygiene:
  - Keep context small: summarize long sections instead of pasting them; only load extra files when needed.
  - Avoid deep reference-chasing: prefer opening only files directly linked from `SKILL.md` unless you're blocked.
  - When variants exist (frameworks, providers, domains), pick only the relevant reference file(s) and note that choice.
- Safety and fallback: If a skill can't be applied cleanly (missing files, unclear instructions), state the issue, pick the next-best approach, and continue.

## READMEの使い方（ローカル開発）
- ローカル環境のセットアップや起動確認を依頼されたら、必ず `README.md` の「🚀 ローカル開発環境構築」に従う。
- DBは Docker ではなくローカル起動の PostgreSQL 17 を前提とする。
- バックエンド起動時は `--spring.profiles.active=local` を指定する。
- `backend/src/main/resources/application-local.yml` はローカル専用ファイルとして扱い、コミット対象にしない。
- 手順変更が発生した場合は、実装より先に `README.md` の手順を更新してから作業する。

## PR運用
- PR作成・更新時は必ず `.github/pull_request_template.md` の見出しと順序をそのまま使う。
- テンプレートの各セクションは空欄にせず、未実施項目は未実施であることを明記する（チェックボックスを正確に記載）。
- Issue対応時は `関連Issue` セクションに `Closes #<番号>` を必ず含める。

## コードレビュー運用
- コードレビューを実施する際は、事前に `CODE_REVIEW_GUIDELINE.md` を確認してからレビューする。

## Playwright MCP運用
- スクリーンショット取得は `browser_take_screenshot` を優先して使用する。
- `browser_take_screenshot` 実行時は `filename` に絶対パスを指定する（例: `/Users/.../tmp/evidence.png`）。
- レスポンスは画像バイナリ本体ではなく、保存ファイルへの参照リンクとして扱う。
- `EROFS: read-only file system` が出る場合は、保存先パスを見直す（ルート直下や不正な相対指定を避ける）。
- `既存のブラウザ セッションで開いています。` で起動失敗する場合は、`mcp-chrome` 残留プロセスを停止して再実行する。
