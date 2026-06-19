# Google Calendar Read-Only Integration

This version adds Google Calendar read-only support.

## Required environment variables

```bash
GOOGLE_CALENDAR_ENABLED=true
GOOGLE_CALENDAR_ID=your_calendar_id_or_primary
GOOGLE_CLIENT_EMAIL=service-account@project.iam.gserviceaccount.com
GOOGLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
GOOGLE_PROJECT_ID=your-google-cloud-project-id
```

## Google Cloud setup

1. Go to Google Cloud Console.
2. Create or select a project.
3. Enable Google Calendar API.
4. Create a Service Account.
5. Create a JSON key for that Service Account.
6. Copy `client_email` to `GOOGLE_CLIENT_EMAIL`.
7. Copy `private_key` to `GOOGLE_PRIVATE_KEY`.
8. In Google Calendar, open Calendar Settings.
9. Share the calendar with the service account email.
10. Give at least `See all event details` permission.

## Slack/Render variables

Also keep these existing variables:

```bash
DB_URL=jdbc:postgresql://...
DB_USERNAME=...
DB_PASSWORD=...
GEMINI_API_KEY=...
SLACK_BOT_TOKEN=xoxb-...
SLACK_DEFAULT_CHANNEL=Cxxxxxx
```

## New APIs

```http
GET /api/settings
PUT /api/settings
GET /api/calendar/today
GET /api/calendar/events?date=2026-06-20
```

## Setting example

```json
{
  "morningScrumTime": "09:00:00",
  "eveningReviewTime": "21:00:00",
  "morningScrumEnabled": true,
  "eveningReviewEnabled": true,
  "calendarReadEnabled": true,
  "calendarReminderEnabled": true,
  "eventReminderMinutes": 10,
  "timezone": "Asia/Seoul",
  "slackChannelId": "C0123456789"
}
```

## Supported Slack questions

- 오늘 물 얼마나 마셨어?
- 오늘 몇 칼로리 먹었어?
- 오늘 일정 뭐야?
- 내일 일정 알려줘
- 스크럼이랑 회고 몇 시야?
- 이번 달 지출 얼마야?
