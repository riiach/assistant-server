# Assistant Server

A personal AI assistant ("Personal OS") backend that uses Slack as its interface. Send a message via Slack and Gemini parses it to automatically log tasks, health, finances, goals, and more. It can also answer questions based on the data already stored, and sends scheduled morning scrums / evening retrospectives. Google Calendar integration lets it read your schedule as well.

## Features

### 1. Log via Chat
Send a natural-language message through a Slack slash command, and Gemini parses it into the appropriate record:
- Finance records / accounts / budget categories / finance summaries
- Health records / workout records (including calorie estimation from Korean food messages, e.g. "오늘 치킨 4조각 먹었어")
- Tasks / projects / goals / milestones
- Habit trackers & checks / journals / savings goals

### 2. Retrieval (Q&A)
Answers direct questions using the data already stored in the DB, e.g.:
- "오늘 물 얼마나 마셨더라?" (How much water did I drink today?)
- "이번 달 얼마 썼어?" (How much have I spent this month?)
- "진행 중인 목표 알려줘" (List my active goals)
- "남은 할 일 뭐야?" (What tasks are left?)
- "오늘/내일 일정 뭐야?" (What's on today's/tomorrow's schedule?)

### 3. Reminders
Create reminders from natural language, e.g. "매일 오후 2시에 물 마시라고 알려줘" (Remind me to drink water every day at 2pm).

### 4. Scheduled Notifications
- Sends morning scrum / evening retrospective messages to Slack at configured times
- Schedule is stored per-user in the DB via `AssistantSetting` and loaded dynamically
- Sends reminder notifications before calendar events start

### 5. Google Calendar Integration (read-only)
- Read-only calendar access via a Service Account JWT OAuth flow
- Today's events are included in the morning scrum / evening retrospective
- `GET /api/calendar/today`, `GET /api/calendar/events?date=...`

### 6. Planner
Given a goal, automatically generates milestones, tasks, and habits to support it.

## Tech Stack

| Area | Technology |
| --- | --- |
| Language / Framework | Java 17, Spring Boot 3.5 |
| Web | Spring Web, Spring WebFlux (WebClient) |
| Database | PostgreSQL, Spring Data JPA |
| Security | Spring Security |
| Validation | Spring Validation |
| AI | Google Gemini API (`gemini-2.5-flash`) |
| Messaging | Slack API (slash commands, async `response_url` callback) |
| Calendar | Google Calendar API (Service Account JWT) |
| Build | Gradle |
| Deployment | Docker |

## Architecture

The codebase follows a feature (domain) based package structure.

```
src/main/java/com/assistant/server/
├── assistant/
│   ├── ai/
│   │   ├── memory/       # Long-term memory (MemoryContext) service
│   │   ├── notification/ # Morning scrum / evening retrospective scheduler
│   │   ├── planner/      # Goal → milestones/tasks/habits generation
│   │   ├── reminder/     # Natural-language reminder creation
│   │   └── retrieval/    # DB-backed question answering
│   ├── application/      # Use-case services
│   ├── domain/
│   │   ├── entity/       # Task, Goal, FinanceRecord, HealthRecord, etc.
│   │   ├── enums/        # FinanceType, HealthType, RecordStatus
│   │   └── repository/   # Spring Data JPA repositories
│   └── presentation/     # REST controllers & DTOs
├── calendar/              # Read-only Google Calendar client/service
├── gemini/                 # Gemini API integration (request/response parsing)
├── slack/                  # Slack message & slash command handling
└── global/config/           # Security, async, and WebClient configuration
```

Each submodule under `ai/` has its own README.

## API Overview

| Endpoint | Description |
| --- | --- |
| `POST /api/slack/commands` | Handles the Slack slash command (replies immediately with "기록 중이야...", then sends the result asynchronously) |
| `GET /api/settings`, `PUT /api/settings` | Get/update per-user notification & calendar settings |
| `GET /api/calendar/today` | Today's calendar events |
| `GET /api/calendar/events?date=YYYY-MM-DD` | Calendar events for a given date |
| `GET/POST/PUT/DELETE /api/tasks`, `/api/goals`, `/api/milestones`, `/api/projects` | CRUD for tasks / goals / milestones / projects |
| `GET/POST/PUT/DELETE /api/finance-records`, `/api/health-records`, `/api/workout-records` | CRUD for finance / health / workout records |
| `GET/POST/PUT/DELETE /api/habit-trackers`, `/api/habit-checks` | CRUD for habit trackers & checks |
| `GET/POST /api/savings-goals` | Savings goals |
| `GET/POST /api/journal` | Journal entries |
| `GET/POST /api/assistant-logs` | Raw conversation/processing logs |

See the controllers under each domain's `presentation` package for the full route list.

## Data Model

Key entities (`assistant/domain/entity`):

- `Task`, `Goal`, `Milestone`, `Project`
- `FinanceRecord`, `Account`, `BudgetCategory`, `FinanceSummary`
- `HealthRecord`, `WorkoutRecord`
- `HabitTracker`, `HabitCheck`
- `Journal`, `SavingsGoal`
- `AssistantSetting` (per-user notification/calendar settings)
- `AssistantLog` (raw message/processing log)
- `CalendarReminderLog` (calendar reminder send history)

## Getting Started

### Prerequisites

- Java 17
- PostgreSQL
- A Gemini API key
- A Slack app (bot token, slash command)
- (Optional) a Service Account for Google Calendar integration

### Environment Variables

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/assistant
DB_USERNAME=
DB_PASSWORD=

# Gemini
GEMINI_API_KEY=

# Slack
SLACK_BOT_TOKEN=xoxb-...
SLACK_DEFAULT_CHANNEL=            # optional, channel ID for scheduled messages (e.g. C0123456789)

# Google Calendar (optional, read-only)
GOOGLE_CALENDAR_ENABLED=false
GOOGLE_CALENDAR_ID=primary
GOOGLE_CLIENT_EMAIL=
GOOGLE_PRIVATE_KEY=
GOOGLE_PROJECT_ID=assistant-server
```

See [`GOOGLE_CALENDAR_SETUP.md`](./GOOGLE_CALENDAR_SETUP.md) for the full Google Calendar setup guide.

### Build & Run

```bash
./gradlew build
./gradlew bootRun
```

Or with Docker:

```bash
docker build -t assistant-server .
docker run -p 8080:8080 --env-file .env assistant-server
```

### Tests

```bash
./gradlew test
```

## Example Settings Payload

Example of the per-user notification/calendar settings managed via `AssistantSetting`:

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

## Related Docs

- [`AI_PERSONAL_OS_V2_PLAN.md`](./AI_PERSONAL_OS_V2_PLAN.md) — feature history and design notes
- [`GOOGLE_CALENDAR_SETUP.md`](./GOOGLE_CALENDAR_SETUP.md) — Google Calendar integration setup guide

---

**Note:** This project was built for learning/educational purposes. It is not a finished, production-ready product, and some features may not work correctly or may be incomplete (e.g. the Google Calendar integration was added incrementally and has known limitations). Use it as a reference rather than something to deploy as-is.
