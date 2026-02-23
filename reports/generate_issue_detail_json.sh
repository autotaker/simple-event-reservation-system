#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  ./reports/generate_issue_detail_json.sh \
    --issues "28,29,30,31" \
    --output ./reports/detail.json

Options:
  --repo OWNER/NAME      GitHub repository (default: autotaker/simple-event-reservation-system)
  --issues LIST          Comma/space separated issue numbers (e.g. "42,30 31")
  --issues-file PATH     Text file containing issue numbers (one per line or comma separated)
  --output PATH          Output JSON path (default: ./reports/detail.json)
  -h, --help             Show this help

You can also pass issue numbers as positional args:
  ./reports/generate_issue_detail_json.sh 42 30 39
EOF
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "error: required command not found: $1" >&2
    exit 1
  }
}

REPO="autotaker/simple-event-reservation-system"
OUTPUT="./reports/detail.json"
ISSUES_TEXT=""
ISSUES_FILE=""
POSITIONAL=()

while [[ $# -gt 0 ]]; do
  case "$1" in
    --repo)
      REPO="${2:-}"
      shift 2
      ;;
    --issues)
      ISSUES_TEXT="${2:-}"
      shift 2
      ;;
    --issues-file)
      ISSUES_FILE="${2:-}"
      shift 2
      ;;
    --output)
      OUTPUT="${2:-}"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      POSITIONAL+=("$1")
      shift
      ;;
  esac
done

require_cmd gh
require_cmd jq

owner="${REPO%%/*}"
name="${REPO##*/}"
if [[ -z "$owner" || -z "$name" || "$owner" == "$name" ]]; then
  echo "error: invalid --repo format. expected OWNER/NAME, got: $REPO" >&2
  exit 1
fi

raw_issues=""
if [[ -n "$ISSUES_TEXT" ]]; then
  raw_issues+=$'\n'"$ISSUES_TEXT"
fi
if [[ -n "$ISSUES_FILE" ]]; then
  if [[ ! -f "$ISSUES_FILE" ]]; then
    echo "error: --issues-file not found: $ISSUES_FILE" >&2
    exit 1
  fi
  raw_issues+=$'\n'"$(cat "$ISSUES_FILE")"
fi
if [[ ${#POSITIONAL[@]} -gt 0 ]]; then
  raw_issues+=$'\n'"${POSITIONAL[*]}"
fi

if [[ -z "${raw_issues//$'\n'/}" ]]; then
  echo "error: no issue numbers provided." >&2
  usage
  exit 1
fi

issue_numbers=()
while IFS= read -r n; do
  issue_numbers+=("$n")
done < <(
  printf '%s\n' "$raw_issues" \
    | tr ',\t' '\n ' \
    | tr ' ' '\n' \
    | sed '/^$/d' \
    | grep -E '^[0-9]+$' \
    | awk '!seen[$0]++'
)

if [[ ${#issue_numbers[@]} -eq 0 ]]; then
  echo "error: failed to parse issue numbers from input." >&2
  exit 1
fi

QUERY=$(cat <<'EOF'
query($owner:String!, $name:String!, $number:Int!) {
  repository(owner:$owner, name:$name) {
    issue(number:$number) {
      number
      title
      url
      body
      createdAt
      updatedAt
      closedAt
      state
      author { login }
      comments(first:100) {
        totalCount
        nodes {
          author { login }
          body
          createdAt
          updatedAt
          url
        }
      }
      timelineItems(first:100, itemTypes:[CLOSED_EVENT,CROSS_REFERENCED_EVENT]) {
        nodes {
          __typename
          ... on ClosedEvent {
            createdAt
            actor { login }
            closer {
              __typename
              ... on PullRequest {
                number
                title
                url
                state
                isDraft
                createdAt
                mergedAt
                closedAt
                additions
                deletions
                changedFiles
                comments { totalCount }
                reviews { totalCount }
                reviewThreads { totalCount }
                closingIssuesReferences(first:20) { nodes { number } }
              }
            }
          }
          ... on CrossReferencedEvent {
            createdAt
            source {
              __typename
              ... on PullRequest {
                number
                title
                url
                state
                isDraft
                createdAt
                mergedAt
                closedAt
                additions
                deletions
                changedFiles
                comments { totalCount }
                reviews { totalCount }
                reviewThreads { totalCount }
                closingIssuesReferences(first:20) { nodes { number } }
              }
            }
          }
        }
      }
    }
  }
}
EOF
)

tmpdir="$(mktemp -d)"
trap 'rm -rf "$tmpdir"' EXIT

for issue_number in "${issue_numbers[@]}"; do
  out="$tmpdir/issue-${issue_number}.json"
  if ! gh api graphql \
      -f query="$QUERY" \
      -F owner="$owner" \
      -F name="$name" \
      -F number="$issue_number" > "$out"; then
    echo "warn: failed to fetch issue #$issue_number" >&2
    continue
  fi

  if [[ "$(jq -r '.data.repository.issue == null' "$out")" == "true" ]]; then
    echo "warn: issue #$issue_number not found in $REPO" >&2
    rm -f "$out"
    continue
  fi
done

shopt -s nullglob
issue_files=("$tmpdir"/issue-*.json)
if [[ ${#issue_files[@]} -eq 0 ]]; then
  echo "error: no issues were fetched successfully." >&2
  exit 1
fi

mkdir -p "$(dirname "$OUTPUT")"

jq -s --arg repo "$REPO" '
  map(.data.repository.issue) as $issues
  | {
      generated_at: (now | todateiso8601),
      repository: $repo,
      issue_count: ($issues | length),
      issues: (
        $issues
        | map(
            . as $issue
            | (
                [
                  .timelineItems.nodes[]?
                  | if .__typename == "ClosedEvent"
                       and (.closer != null)
                       and (.closer.__typename == "PullRequest")
                    then
                      (.closer + { linkedAt: .createdAt, linkType: "closed_event" })
                    elif .__typename == "CrossReferencedEvent"
                       and (.source != null)
                       and (.source.__typename == "PullRequest")
                    then
                      (.source + { linkedAt: .createdAt, linkType: "cross_referenced_event" })
                    else
                      empty
                    end
                ]
                | group_by(.number)
                | map({
                    pr_number: .[0].number,
                    pr_title: .[0].title,
                    pr_url: .[0].url,
                    pr_state: .[0].state,
                    pr_is_draft: .[0].isDraft,
                    pr_additions: .[0].additions,
                    pr_deletions: .[0].deletions,
                    pr_changed_files: .[0].changedFiles,
                    pr_changed_lines: ((.[0].additions // 0) + (.[0].deletions // 0)),
                    pr_comment_count: (.[0].comments.totalCount // 0),
                    pr_review_count: (.[0].reviews.totalCount // 0),
                    pr_review_thread_count: (.[0].reviewThreads.totalCount // 0),
                    closes_issue_numbers: (.[0].closingIssuesReferences.nodes | map(.number)),
                    link_event_count: length,
                    link_timestamps: ([.[].linkedAt] | sort),
                    timestamps: {
                      created_at: .[0].createdAt,
                      merged_at: .[0].mergedAt,
                      closed_at: .[0].closedAt
                    }
                  })
              ) as $all_prs
            | {
                issue_number: .number,
                issue_title: .title,
                issue_url: .url,
                issue_body: .body,
                issue_comment_count: .comments.totalCount,
                issue_comments: (
                  .comments.nodes
                  | sort_by(.createdAt)
                  | map({
                      author: (if .author == null then null else .author.login end),
                      body: .body,
                      url: .url,
                      timestamps: {
                        created_at: .createdAt,
                        updated_at: .updatedAt
                      }
                    })
                ),
                fixing_pull_requests: (
                  $all_prs
                  | map(select((.closes_issue_numbers | index($issue.number)) != null))
                ),
                related_pull_requests: (
                  $all_prs
                  | map(select((.closes_issue_numbers | index($issue.number)) == null))
                ),
                timestamps: {
                  issue_created_at: .createdAt,
                  issue_updated_at: .updatedAt,
                  issue_closed_at: .closedAt
                }
              }
          )
        | sort_by(.timestamps.issue_closed_at)
      )
    }
' "${issue_files[@]}" > "$OUTPUT"

echo "generated: $OUTPUT"
echo "issue_count: $(jq -r '.issue_count' "$OUTPUT")"
