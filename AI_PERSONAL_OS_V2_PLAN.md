# AI Personal OS v2 implemented updates

This zip expands the assistant beyond simple logging.

## Added/fixed

- Slack slash command now uses asynchronous `response_url` callback.
- Slack returns immediately with `기록 중이야...` and posts the final result when processing finishes.
- Assistant can answer direct DB-backed questions such as:
  - `오늘 물 얼마나 마셨더라?`
  - `이번 달 얼마 썼어?`
  - `진행 중인 목표 알려줘`
  - `남은 할 일 뭐야?`
- Gemini prompt now supports:
  - finances
  - health records
  - workouts
  - tasks
  - goals
  - milestones
  - projects
  - habit trackers
  - journals
  - savings goals
  - budget categories
  - finance summaries
  - calendar event placeholders
- Added finance expansion entities:
  - Account
  - BudgetCategory
  - FinanceSummary
- Added budget risk warning logic.
- Added morning scrum and evening retrospective scheduler.
- Added optional `SLACK_DEFAULT_CHANNEL` environment variable for scheduled Slack briefings.
- Added Google Calendar placeholder client for future OAuth integration.

## Important env vars

- DB_URL
- DB_USERNAME
- DB_PASSWORD
- GEMINI_API_KEY
- SLACK_BOT_TOKEN
- SLACK_DEFAULT_CHANNEL optional, Slack channel ID like C0123456789

## Known limitation

Google Calendar event creation is intentionally left as a placeholder because Google Calendar OAuth/service-account setup is required.
