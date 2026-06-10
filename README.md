# CompassChat

**A store-and-forward messaging platform for the Community Compass ecosystem**

CompassChat is a Java-based, Slack-inspired communication platform built as the primary internal messaging backbone for [Community Compass](#about-community-compass) — an AI-powered community guidance platform that connects residents to housing, resources, and support services.

This project is a ZipCode Wilmington student capstone. It demonstrates full-stack Java development, real-time messaging architecture, role-based access control, and integration across a multi-project ecosystem.

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

CompassChat is the **communication layer** that ties these four sub-projects together, enabling case workers, coordinators, and support staff to collaborate across the platform in real time.

---

## Project Overview

CompassChat is a **store-and-forward messaging system** — messages are persisted to the server and delivered to recipients whenever they are online. This design supports:

- Asynchronous communication between case workers and clients
- Persistent channel history that survives client disconnects
- Audit-ready message records for case documentation
- Cross-team coordination across the four Community Compass sub-projects

### Who Uses CompassChat?

| Role | Use Case |
|---|---|
| **Case Workers** | Communicate directly with clients; coordinate with peers; receive sub-project channel updates |
| **Clients / Residents** | Receive guidance, follow up on housing referrals, ask questions in a secure space |
| **Coordinators / Admins** | Manage channel membership, monitor communication, oversee cross-team workflows |
| **Sub-Project Teams** | Dedicated channels for housing, civic, well-being, and youth services coordination |

---

## Features

### Messaging
- Real-time store-and-forward messaging (WebSocket / long-poll fallback)
- Direct Messages (DMs) between any two users
- Public and private channels
- Threaded replies on any message
- Message edit and delete (with audit trail)
- File and image attachment support
- Read receipts and unread message counts

### Channels
- Create and archive channels
- Role-based channel membership (admin, member, read-only)
- Pinned messages within channels
- Channel descriptions and purpose fields
- System channels linked to each Community Compass sub-project

### Users & Roles
- Role-based access control: `ADMIN`, `CASE_WORKER`, `COORDINATOR`, `CLIENT`, `VOLUNTEER`
- User profiles with contact info and assigned sub-project(s)
- Case worker–client assignment management
- Presence indicators (online / away / offline)

### Notifications
- In-app notification feed
- `@mention` support with targeted notifications
- Channel-level notification preferences (all messages / mentions only / muted)
- Email digest for offline users (configurable)

### Administration
- Admin dashboard: user management, channel oversight, message audit log
- Bulk channel membership management
- Export channel history (CSV / JSON) for case records
- Rate limiting and spam prevention

---

## Architecture

```
┌─────────────────────────────────────────────────┐
│                  CompassChat                    │
│                                                 │
│   ┌─────────────┐       ┌────────────────────┐  │
│   │  React 18   │◄─────►│  Spring Boot API   │  │
│   │  Frontend   │  REST │  (Java 21)         │  │
│   │  (Vite +    │  /WS  │                    │  │
│   │  Tailwind)  │       │  - Auth (JWT)      │  │
│   └─────────────┘       │  - Channels        │  │
│                         │  - Messages        │  │
│                         │  - Users / Roles   │  │
│                         │  - Notifications   │  │
│                         └────────┬───────────┘  │
│                                  │              │
│                         ┌────────▼───────────┐  │
│                         │   PostgreSQL DB    │  │
│                         │   (Messages,       │  │
│                         │   Channels,        │  │
│                         │   Users, Roles)    │  │
│                         └────────────────────┘  │
└─────────────────────────────────────────────────┘
         │              │              │              │
         ▼              ▼              ▼              ▼
   [Civic Sub-    [Housing Sub-  [Well-Being    [Youth Sub-
    Project]       Project]      Sub-Project]   Project]
```

### Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Persistence** | Spring Data JPA / Hibernate |
| **Database** | PostgreSQL |
| **Real-Time** | Spring WebSocket (STOMP) |
| **Auth** | Spring Security + JWT |
| **Build Tool** | Maven or Gradle |
| **Frontend** | React 18, Vite, Tailwind CSS |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |
| **Containerization** | Docker / Docker Compose |

### Key Domain Models

```
User
 ├── id, username, email, passwordHash
 ├── role: ADMIN | CASE_WORKER | COORDINATOR | CLIENT | VOLUNTEER
 ├── assignedSubProject (optional)
 └── presence: ONLINE | AWAY | OFFLINE

Channel
 ├── id, name, description, purpose
 ├── type: PUBLIC | PRIVATE | DIRECT | SYSTEM
 ├── linkedSubProject (optional)
 └── members: List<ChannelMembership>

Message
 ├── id, body, createdAt, editedAt
 ├── sender: User
 ├── channel: Channel
 ├── parentMessage: Message (for threads)
 └── attachments: List<Attachment>

ChannelMembership
 ├── user: User
 ├── channel: Channel
 ├── memberRole: ADMIN | MEMBER | READ_ONLY
 └── notificationPreference: ALL | MENTIONS | MUTED
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+ or Gradle 8+
- PostgreSQL 15+
- Node.js 20+ (for frontend development)
- Docker (optional, for containerized setup)

### Clone the Repository

```bash
git clone https://github.com/zipcode-wilmington/<your-repo-name>.git
cd <your-repo-name>
```

### Database Setup

```sql
CREATE DATABASE compasschat;
CREATE USER compasschat_user WITH ENCRYPTED PASSWORD 'changeme';
GRANT ALL PRIVILEGES ON DATABASE compasschat TO compasschat_user;
```

### Backend Configuration

Copy the example environment file and update values:

```bash
cp src/main/resources/application.example.properties src/main/resources/application.properties
```

Key configuration properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/compasschat
spring.datasource.username=compasschat_user
spring.datasource.password=changeme
spring.jpa.hibernate.ddl-auto=update

jwt.secret=your-secret-key-here
jwt.expiration-ms=86400000

# Cross-origin for frontend dev server
cors.allowed-origins=http://localhost:5173
```

### Run the Backend

```bash
# Maven
./mvnw spring-boot:run

# Gradle
./gradlew bootRun
```

The API will be available at `http://localhost:8080`.

### Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

The UI will be available at `http://localhost:5173`.

### Docker Compose (Full Stack)

```bash
docker compose up --build
```

This starts PostgreSQL, the Spring Boot API, and the React frontend together.

---

## API Reference

Base URL: `http://localhost:8080/api/v1`

All endpoints (except auth) require a `Bearer <token>` Authorization header.

### Authentication

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/auth/register` | Register a new user |
| `POST` | `/auth/login` | Login, returns JWT |
| `POST` | `/auth/logout` | Invalidate session token |

### Users

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/users` | List all users (admin) |
| `GET` | `/users/{id}` | Get user profile |
| `PUT` | `/users/{id}` | Update user profile |
| `GET` | `/users/me` | Get current user |
| `PUT` | `/users/me/presence` | Update presence status |

### Channels

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/channels` | List accessible channels |
| `POST` | `/channels` | Create a new channel |
| `GET` | `/channels/{id}` | Get channel details |
| `PUT` | `/channels/{id}` | Update channel info |
| `DELETE` | `/channels/{id}` | Archive a channel |
| `POST` | `/channels/{id}/members` | Add member to channel |
| `DELETE` | `/channels/{id}/members/{userId}` | Remove member |

### Messages

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/channels/{id}/messages` | Get channel message history |
| `POST` | `/channels/{id}/messages` | Post a message |
| `PUT` | `/messages/{id}` | Edit a message |
| `DELETE` | `/messages/{id}` | Delete a message |
| `POST` | `/messages/{id}/replies` | Reply in thread |
| `GET` | `/messages/{id}/replies` | Get thread replies |

### Direct Messages

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/dm` | List DM conversations |
| `POST` | `/dm/{userId}` | Start or continue a DM |

### WebSocket

Connect at `ws://localhost:8080/ws` using STOMP.

| Destination | Direction | Description |
|---|---|---|
| `/app/channel/{id}/send` | Client → Server | Send a message |
| `/topic/channel/{id}` | Server → Client | Receive channel messages |
| `/user/queue/notifications` | Server → Client | Personal notifications |
| `/user/queue/dm` | Server → Client | Incoming DMs |

---

## Channel Structure

CompassChat ships with a set of pre-seeded **system channels** aligned to the Community Compass sub-projects, plus standard coordination channels.

### System Channels

| Channel | Purpose |
|---|---|
| `#civic-team` | Internal coordination for the Civic Guidance & Community Updates sub-project |
| `#housing-team` | Case worker coordination for housing navigation and voucher support |
| `#wellbeing-team` | Community support coordinators and volunteer network communication |
| `#youth-services-team` | Youth transition counselors and pathway coordinators |

### Standard Channels

| Channel | Purpose |
|---|---|
| `#general` | Organization-wide announcements and general discussion |
| `#case-workers` | Case worker peer support and shared practice |
| `#admin-ops` | Administrative and operational coordination |
| `#volunteers` | Volunteer scheduling and coordination |
| `#tech-support` | Platform support and issue reporting |

### Client Channels

Client-facing channels are created dynamically as **private channels** when a case is opened. Naming convention:

```
#case-{caseId}-{clientLastName}
```

Only the assigned case worker(s) and the client are members. Coordinators with appropriate roles may observe but do not participate by default.

---

## Related Sub-Projects

CompassChat is the communication backbone for the Community Compass platform. The four sub-projects below are companion capstone repositories within the same ecosystem.

### Civic Guidance & Community Updates
> Verified community resources, local assistance programs, seasonal opportunities, and policy updates — with "Why It Matters" civic briefings that help residents understand the impact of news and legislation.

- **Repo:** [community-compass-civic](#)
- **Stack:** React 18, Spring Boot, PostgreSQL
- **CompassChat Integration:** Staff coordinate in `#civic-team`; policy update alerts can be posted to `#general`

---

### Personalized Housing Navigation
> Eligibility-based housing matching, interactive Leaflet maps, and an AI Housing Assistant grounded in real listings, eligibility requirements, and user preferences.

- **Repo:** [community-compass-housing](#)
- **Stack:** React 18, FastAPI, PostgreSQL / Supabase, Leaflet, OpenAI / Claude
- **CompassChat Integration:** Case workers coordinate in `#housing-team`; client housing threads use private case channels

---

### Community Support & Well-Being
> Resource discovery, mood and wellness check-ins, appointment and reminder management, volunteer network connectivity, and AI-guided recommendations.

- **Repo:** [community-compass-wellbeing](#)
- **Stack:** React 18, Spring Boot, PostgreSQL / Supabase, Claude / Ollama
- **CompassChat Integration:** Coordinators and volunteers communicate in `#wellbeing-team`; support requests can trigger DM notifications to assigned workers

---

### Youth Transition Pathways
> AI-assisted intake and structured guidance plans for young adults navigating housing, employment, education, transportation, and financial stability simultaneously.

- **Repo:** [community-compass-youth](#)
- **Stack:** React 18, Spring Boot, FastAPI, PostgreSQL, OpenAI / Claude
- **CompassChat Integration:** Youth counselors coordinate in `#youth-services-team`; transition plan milestones can generate automated check-in messages

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
