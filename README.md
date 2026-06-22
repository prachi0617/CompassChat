# CompassChat

**A messaging and AI-assistant platform for the Community Compass ecosystem**

CompassChat is a full-stack, Slack-inspired communication platform built as the internal messaging backbone for [Community Compass](#about-community-compass) — an AI-powered community guidance platform that connects residents to housing, resources, and support services. It pairs real-time channels and direct messages with an AI Assistant that surfaces resources and can hand off to a human navigator.

This project is a ZipCode Wilmington student capstone. It demonstrates full-stack Java development, real-time messaging over WebSocket/STOMP, role-based access control, an AI resource assistant with provider fallback, and integration across a multi-project ecosystem.

---

## Table of Contents

- [About Community Compass](#about-community-compass)
- [Project Overview](#project-overview)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Channel Structure](#channel-structure)
- [Related Sub-Projects](#related-sub-projects)
- [Roadmap / Future Versions](#roadmap--future-versions)
- [Team](#team)
- [License](#license)

---

## About Community Compass

Community Compass is an AI-powered community guidance platform designed to help residents find housing, resources, opportunities, and support through one trusted entry point. It addresses the fragmented nature of social services by unifying four core capabilities:

| Sub-Project | Description |
|---|---|
| **Civic Guidance & Community Updates** | Verified community resources, policy updates, and "Why It Matters" civic briefings |
| **Personalized Housing Navigation** | Eligibility-based housing matching, interactive maps, and an AI Housing Assistant |
| **Community Support & Well-Being** | Resource discovery, wellness check-ins, volunteer networks, and AI-guided recommendations |
| **Youth Transition Pathways** | AI-assisted intake and structured guidance plans for young adults navigating independence |

CompassChat is the **communication layer** that ties these four sub-projects together, enabling residents and support staff to collaborate and access resources in real time.

---

## Project Overview

CompassChat persists messages to the server and delivers them to recipients in real time over WebSocket. This design supports:

- Real-time communication across channels and direct messages
- Persistent channel and DM history that survives client disconnects
- An AI Assistant that classifies intent, returns matching resources, and escalates to a human navigator when needed
- Cross-team coordination across the four Community Compass sub-projects

### Who Uses CompassChat?

| Role | Use Case |
|---|---|
| **Residents / Clients** | Ask the AI Assistant for help, receive resource recommendations, follow up with a navigator |
| **Case Workers / Navigators** | Communicate directly with residents; coordinate with peers |
| **Coordinators / Admins** | Manage channel membership and oversee communication |
| **Sub-Project Teams** | Channels aligned to housing, civic, well-being, and youth services |

---

## Features

### Messaging
- Real-time messaging over WebSocket (STOMP + SockJS)
- Direct Messages (DMs) between users
- Public, private, direct, and system channel types
- Message edit and delete (soft-delete) with an audit-log history endpoint
- Typing indicators and presence broadcasts (online / away / offline)
- Channel read tracking and unread counts

### AI Assistant
- In-app AI Assistant for resource discovery, grounded in the Community Compass service directory
- Quick-action prompts: food, housing, youth resources, and "how you are feeling"
- Resource results rendered as cards, paginated ("Would you like to see more?")
- Mood check-ins with resource recommendations
- Crisis detection block (988 Suicide & Crisis Lifeline, 741741 Crisis Text Line)
- Escalation / handoff to a human navigator or sub-project channel
- Provider fallback chain: **Groq** (Llama 3) → **Ollama** (local) → **rule-based** responses

### Channels & Users
- Create, update, and archive channels
- Channel membership management (add / remove members, per-member role and notification preference)
- Role-based access control (see roles below)
- User profiles with presence status and an assigned sub-project field

### Mentions
- `@mention` tracking with unread counts
- Real-time mention delivery over WebSocket (`/user/queue/mentions`)

> See [Roadmap / Future Versions](#roadmap--future-versions) for features planned but not yet implemented (threaded replies, file attachments, read receipts, email digests, pinned messages, export, rate limiting, and more).

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  CompassChat                     │
│                                                  │
│   ┌─────────────┐       ┌────────────────────┐   │
│   │  React 19   │◄─────►│  Spring Boot API   │   │
│   │  Frontend   │  REST │  (Java 17)         │   │
│   │  (Vite +    │  /WS  │                    │   │
│   │  Tailwind)  │       │  - Auth (JWT)      │   │
│   │  :5173      │       │  - Channels / DMs  │   │
│   └─────────────┘       │  - Messages        │   │
│                         │  - Users / Roles   │   │
│                         │  - Mentions / Mood │   │
│                         │  - AI Assistant    │   │
│                         │  :8081             │   │
│                         └────────┬───────────┘   │
│                                  │               │
│                         ┌────────▼───────────┐   │
│                         │  H2 (in-memory,    │   │
│                         │  PostgreSQL mode)  │   │
│                         └────────┬───────────┘   │
│                                  │               │
│                         ┌────────▼───────────┐   │
│                         │  AI Providers:     │   │
│                         │  Groq → Ollama →   │   │
│                         │  rule-based        │   │
│                         └────────────────────┘   │
└─────────────────────────────────────────────────┘
         │              │              │              │
         ▼              ▼              ▼              ▼
   [Civic Sub-    [Housing Sub-  [Well-Being    [Youth Sub-
    Project]       Project]      Sub-Project]   Project]
```

### Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.3.5 |
| **Persistence** | Spring Data JPA / Hibernate |
| **Database** | H2 in-memory (PostgreSQL compatibility mode); `create-drop` on startup |
| **Real-Time** | Spring WebSocket (STOMP over SockJS) |
| **Auth** | Spring Security + JWT (JJWT 0.11.5) |
| **AI** | Spring AI (OpenAI-compatible client) → Groq `llama3-8b-8192`; Ollama fallback; rule-based fallback |
| **Build Tool** | Maven |
| **Frontend** | React 19, Vite, Tailwind CSS, Zustand, React Router |
| **Real-Time (FE)** | @stomp/stompjs, sockjs-client |
| **Testing** | JUnit 5, Spring Boot Test, Spring Security Test |

> **Note:** The current build uses an H2 in-memory database that re-seeds on every restart (`create-drop`). PostgreSQL and containerized deployment are planned — see [Roadmap](#roadmap--future-versions).

### Roles

`MEMBER`, `MODERATOR`, `ADMIN`, `CASE_WORKER`, `COORDINATOR`, `CLIENT`, `VOLUNTEER`

### Key Domain Models

```
User
 ├── id, username, email, passwordHash
 ├── role: MEMBER | MODERATOR | ADMIN | CASE_WORKER | COORDINATOR | CLIENT | VOLUNTEER
 ├── assignedSubProject (optional)
 └── presence: ONLINE | AWAY | OFFLINE

Channel
 ├── id, name, description, purpose
 ├── type: PUBLIC | PRIVATE | DIRECT | SYSTEM
 ├── linkedSubProject (optional)
 └── archived

ChannelMember
 ├── channelId, userId, lastReadAt, muted
 ├── memberRole: ADMIN | MEMBER | READ_ONLY
 └── notificationPreference: ALL | MENTIONS | MUTED

Message
 ├── id, content, createdAt, editedAt, deleted
 ├── senderId
 └── channelId

DirectMessage
 ├── id, content, createdAt, editedAt, deleted
 ├── senderId
 └── recipientId

MessageAuditLog
 ├── messageId, action, performedBy
 └── previousContent, newContent

Mention   ── messageId, channelId, senderUserId, mentionedUserId, read
Mood      ── userId, moodType, note
Notification (minimal) ── title, message, read
EscalationLog ── userId, contextMessage
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+ (for the frontend)
- (Optional) A Groq API key for live AI responses; without one, the assistant uses local/rule-based fallbacks

### Clone the Repository

```bash
git clone https://github.com/zipcode-wilmington/<your-repo-name>.git
cd <your-repo-name>
```

### Run the Backend

The backend uses an H2 in-memory database — no external database setup is required. Demo data is seeded automatically on startup.

```bash
cd backend
./mvnw spring-boot:run
```

The API runs at `http://localhost:8081`.

Optional environment variables:

```bash
# Enable live AI responses via Groq (otherwise rule-based / Ollama fallback is used)
export GROQ_API_KEY=your-groq-key

# Optional: point at a local Ollama instance
export OLLAMA_URL=http://localhost:11434
```

Relevant configuration (`backend/src/main/resources/application.properties`):

```properties
server.port=8081

spring.datasource.url=jdbc:h2:mem:compasschat;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true

jwt.expiration-ms=86400000

# AI (Groq, OpenAI-compatible)
spring.ai.openai.base-url=https://api.groq.com/openai
spring.ai.openai.chat.options.model=llama3-8b-8192
```

### Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The UI runs at `http://localhost:5173` and proxies `/api` and `/ws` to the backend on port 8081.

The app **auto-logs in as a demo resident** (`demo-resident` / `demo123`) on boot. If the backend is unreachable, the chat falls back to an offline mock mode so the UI is still demonstrable.

---

## API Reference

Base URL: `http://localhost:8081/api`

All endpoints except `/auth/**` and `/ai/**` require an `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new user, returns JWT |
| `POST` | `/auth/login` | Login, returns JWT |

> JWT is stateless — there is no logout endpoint; clients discard the token.

### Users

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/users` | List users |
| `GET` | `/users/{id}` | Get user profile |
| `PUT` | `/users/{id}` | Update user profile |
| `GET` | `/users/me` | Get current user |
| `PUT` | `/users/me/presence` | Update presence status |

### Channels

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/channels` | List accessible channels |
| `GET` | `/channels/mine` | List the current user's channel memberships |
| `POST` | `/channels` | Create a channel |
| `GET` | `/channels/{id}` | Get channel details |
| `PUT` | `/channels/{id}` | Update channel info |
| `POST` | `/channels/{id}/archive` | Archive a channel |
| `POST` | `/channels/{id}/read` | Mark channel as read |
| `GET` | `/channels/{id}/unread` | Get unread count |
| `GET` | `/channels/{id}/members` | List channel members |
| `POST` | `/channels/{id}/members` | Add a member |
| `DELETE` | `/channels/{id}/members/{userId}` | Remove a member |

### Messages

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/channels/{channelId}/messages` | Get channel message history (paginated) |
| `POST` | `/channels/{channelId}/messages` | Post a message |
| `PUT` | `/messages/{id}` | Edit a message |
| `DELETE` | `/messages/{id}` | Soft-delete a message |
| `GET` | `/messages/{id}/history` | Get a message's edit/audit history |

### Direct Messages

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/dm/{recipientId}` | Send a direct message |
| `GET` | `/dm/{otherUserId}` | Get a DM conversation (paginated) |
| `DELETE` | `/dm/{id}` | Soft-delete a direct message |

### Mentions

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/mentions/me` | List the current user's mentions |
| `GET` | `/mentions/me/unread` | List unread mentions |
| `GET` | `/mentions/me/unread/count` | Unread mention count (badge) |
| `POST` | `/mentions/{id}/read` | Mark a mention as read |

### Mood

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/moods` | Log a mood entry; returns recommended resources |
| `GET` | `/moods/me` | Get the current user's mood history |
| `GET` | `/moods/{id}` | Get a single mood entry |

### AI Assistant

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/ai/chat` | Chat with the AI Assistant (message + history); unauthenticated access allowed |
| `POST` | `/ai/escalate` | Log an escalation to a human navigator |

### WebSocket

Connect at `ws://localhost:8081/ws` using STOMP over SockJS (Bearer token in the connect headers).

| Destination | Direction | Description |
|---|---|---|
| `/app/channels/{id}/messages` | Client → Server | Send a message to a channel |
| `/app/channels/{id}/typing` | Client → Server | Send a typing indicator |
| `/topic/channels/{id}` | Server → Client | Receive channel messages |
| `/topic/channels/{id}/typing` | Server → Client | Receive typing indicators |
| `/topic/presence` | Server → Client | Presence status updates |
| `/user/queue/mentions` | Server → Client | Personal `@mention` notifications |
| `/user/queue/errors` | Server → Client | Session error messages |

---

## Channel Structure

CompassChat seeds demo channels on startup (the H2 database is recreated each run). Channels align to the Community Compass sub-projects alongside general coordination channels.

### Seeded Channels

| Channel | Purpose |
|---|---|
| `#general` | Organization-wide discussion |
| `#futurepath-news` | Youth / FuturePath program updates and resources |
| `#homematch-help` | Housing referrals and HomeMatch support |
| `#firststep-news` | Community resources, policy updates, news |
| `#kindconnect-wellbeing` | Wellness check-ins and support |

### System / Team Channels

Additional team channels (`#civic-team`, `#housing-team`, `#wellbeing-team`, `#youth-services-team`) and operational channels (`#case-workers`, `#admin-ops`, `#volunteers`, `#tech-support`) are seeded as `SYSTEM`-type channels for staff coordination.

### Direct Messages

DMs are `DIRECT`-type channels between two users (e.g. a resident and a navigator). The frontend demo presents an "Admin Team" DM and a mock "Case Worker" DM.

---

## Related Sub-Projects

CompassChat is the communication backbone for the Community Compass platform. The four sub-projects below are companion capstone repositories within the same ecosystem.

### Civic Guidance & Community Updates
> Verified community resources, local assistance programs, seasonal opportunities, and policy updates — with "Why It Matters" civic briefings.

- **Stack:** React, Spring Boot, PostgreSQL
- **CompassChat Integration:** Staff coordinate in `#firststep-news` / `#civic-team`

### Personalized Housing Navigation
> Eligibility-based housing matching, interactive maps, and an AI Housing Assistant grounded in real listings and eligibility requirements.

- **Stack:** React, FastAPI, PostgreSQL / Supabase, Leaflet, OpenAI / Claude
- **CompassChat Integration:** Housing help via the `#homematch-help` channel and AI Assistant

### Community Support & Well-Being
> Resource discovery, mood and wellness check-ins, reminders, volunteer connectivity, and AI-guided recommendations.

- **Stack:** React, Spring Boot, PostgreSQL / Supabase, Claude / Ollama
- **CompassChat Integration:** Wellness support via `#kindconnect-wellbeing` and the mood check-in feature

### Youth Transition Pathways
> AI-assisted intake and structured guidance plans for young adults navigating housing, employment, education, and financial stability.

- **Stack:** React, Spring Boot, FastAPI, PostgreSQL, OpenAI / Claude
- **CompassChat Integration:** Youth resources via `#futurepath-news` and the AI Assistant

---

## Roadmap / Future Versions

The following features are described in the product vision but are **not yet implemented**. They are planned for future versions:

| Feature | Status |
|---|---|
| Threaded replies on messages | Planned |
| File and image attachments | Planned |
| Per-message read receipts | Planned (channel-level read tracking exists today) |
| Pinned messages | Planned |
| In-app notification feed | Planned (mentions are tracked; full notification system is a stub) |
| Email digest for offline users | Planned |
| Export channel history (CSV / JSON) | Planned |
| Rate limiting / spam prevention | Planned |
| Admin dashboard (user/channel/audit management) | Stub endpoints only |
| Case worker ↔ client assignment management | Partial (`assignedSubProject` field exists; no management endpoints) |
| PostgreSQL database + Docker Compose deployment | Planned (currently H2 in-memory) |
| Channel notification preferences UI (all / mentions / muted) | Backend field exists; UI planned |

---

## Team

| Name | Role |
|---|---|
| | Backend Lead |
| | Frontend Lead |
| | Database / Data Modeling |
| | DevOps / Deployment |
| | QA / Testing |

*This project is developed as a capstone for [ZipCode Wilmington](https://zipcodewilmington.com), a nonprofit intensive coding program in Wilmington, Delaware.*

---

## License

This project is developed for educational purposes as part of the ZipCode Wilmington capstone program. See [LICENSE](LICENSE) for details.

---

*Community Compass — Turn fragmented information into coordinated support.*
