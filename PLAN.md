# Community Compass + CompassChat — Frontend Build Plan

**Stack:** React 18 · Vite · Tailwind CSS · Zustand (state) · React Router · @stomp/stompjs (WebSocket)
**Backend:** Spring Boot on `localhost:8080` (already built)
**Demo audience:** Instructors and potential employers
**Author note:** Plan written for execution by two developers; work split suggestions throughout.

---

## 1. Scope decisions (locked)

| Decision | Choice |
|---|---|
| Top-level product | Community Compass (platform) |
| CompassChat role | Feature inside Community Compass, opens as slide-out panel |
| Chat surface architecture | Unified — AI Assistant lives inside CompassChat as pinned conversation |
| Authentication for demo | Auto sign-in as fixed demo user; Login/Register buttons present but unwired |
| Intent classification | Client-side keyword matching, calls existing backend endpoints |
| Live features cut | Presence dots, typing indicators (deferred to v1.1) |
| Sub-project pages | Identical template, just name swap, directs to "speak with [developer name]" |
| Trigger for CompassChat | Floating action button, bottom-right, always visible |
| First-visit AI welcome | Slide-out closed, FAB shows "1 unread" badge, AI conversation pre-populated with greeting |
| Color palette | Mint dominant, pink for CTA/urgent, yellow for highlights |
| Logo | Compass outline with chat bubble (three pending dots) inside |
| Min body font | 15px; 12px for metadata/secondary |

---

## 2. Folder structure

```
frontend/
├── public/
│   └── logo.svg
├── src/
│   ├── assets/
│   │   ├── logo.svg                    # compass + chat bubble
│   │   └── illustrations/              # sub-project hero art (optional)
│   │
│   ├── components/
│   │   ├── community-compass/          # the PLATFORM shell
│   │   │   ├── Layout.jsx              # header + main + FAB
│   │   │   ├── Header.jsx              # logo, nav, Login/Register
│   │   │   ├── Dashboard.jsx           # 4 sub-project cards
│   │   │   ├── SubProjectCard.jsx
│   │   │   └── SubProjectLanding.jsx   # template, takes props
│   │   │
│   │   ├── compass-chat/               # the SLIDE-OUT
│   │   │   ├── ChatTrigger.jsx         # floating button + unread badge
│   │   │   ├── ChatPanel.jsx           # slide-out container
│   │   │   ├── Sidebar.jsx             # channels + DMs + AI pinned
│   │   │   ├── ConversationListItem.jsx
│   │   │   ├── MessageThread.jsx
│   │   │   ├── MessageBubble.jsx       # with Done/Like/Seen pills
│   │   │   ├── Composer.jsx            # rich-text toolbar + textarea
│   │   │   ├── DemoBanner.jsx
│   │   │   └── ai-assistant/
│   │   │       ├── AIConversation.jsx  # specialized thread for AI
│   │   │       ├── intentRouter.js     # keyword matching logic
│   │   │       ├── ResourceCard.jsx
│   │   │       ├── SmartSuggestion.jsx # the highlighted hint card
│   │   │       ├── CrisisBlock.jsx     # crisis safety net resources
│   │   │       └── EscalateCTA.jsx     # "Talk to a human" button
│   │   │
│   │   └── ui/                         # shared atoms
│   │       ├── Button.jsx
│   │       ├── Avatar.jsx
│   │       ├── Pill.jsx                # tag/chip
│   │       └── Toast.jsx
│   │
│   ├── lib/
│   │   ├── api.js                      # fetch wrapper + auth header
│   │   ├── websocket.js                # STOMP client setup
│   │   ├── auth.js                     # demo auto-login on boot
│   │   └── resources.json              # static resource catalog
│   │
│   ├── stores/
│   │   ├── useAuthStore.js             # token, currentUser
│   │   ├── useChatStore.js             # channels, messages, active conv
│   │   └── useAIStore.js               # AI conversation history
│   │
│   ├── pages/
│   │   ├── DashboardPage.jsx
│   │   ├── HomeMatchPage.jsx
│   │   ├── FuturePathPage.jsx
│   │   ├── KindConnectPage.jsx
│   │   └── FirstStepPage.jsx
│   │
│   ├── App.jsx                         # router + global FAB
│   ├── main.jsx
│   └── index.css                       # Tailwind directives + CSS vars
│
├── package.json
├── tailwind.config.js                  # mint/pink/yellow palette
├── vite.config.js
└── README.md
```

---

## 3. Color palette and typography

Add to `tailwind.config.js`:

```js
theme: {
  extend: {
    colors: {
      mint:    { 50:'#F0FBF7', 100:'#D7F4E7', 300:'#7DD9B0', 500:'#3DBE8A', 700:'#1F8C61' },
      pink:    { 50:'#FFF1F5', 300:'#FBA5C0', 500:'#EC4899', 700:'#BE185D' },
      yellow:  { 50:'#FFFBEB', 300:'#FCD34D', 500:'#F59E0B' },
      ink:     { DEFAULT:'#0F172A', 70:'#475569', 50:'#94A3B8' }, // text shades
    },
    fontSize: {
      'meta': '0.75rem',  // 12px — timestamps, tags
      'body': '0.9375rem' // 15px — default body
    }
  }
}
```

**Usage rule:** Mint = trustworthy/calm (default chrome, buttons, links).
Pink = action and urgency (primary CTA, crisis resources, "Talk to a human").
Yellow = attention and notifications (badges, demo banner, smart-suggestion card).

---

## 4. Build milestones

Each milestone ends in a **demo-able state**. Don't move on until the previous demos cleanly.

### M0 — Setup (0.5 day)

- [ ] `npm create vite@latest` (React + JS or TS — your call; JS is faster for this scope)
- [ ] Install: `tailwindcss`, `react-router-dom`, `zustand`, `@stomp/stompjs`, `sockjs-client`
- [ ] Configure Tailwind with palette above
- [ ] Create folder structure (empty files, scaffolded exports)
- [ ] Logo SVG: compass outline with chat bubble (three dots) inside
- [ ] CSS variables for spacing, transitions, slide-out width (`--panel-width: 420px`)

**Demo state:** Vite dev server runs, blank page with logo and palette swatches.

---

### M1 — Community Compass shell (1.5 days)

- [ ] `Layout.jsx`: header + outlet + chat trigger (FAB stub)
- [ ] `Header.jsx`: logo left, nav center, Login/Register right (buttons present, do nothing)
- [ ] `DashboardPage.jsx`: title, subtitle, 4-card grid
- [ ] `SubProjectCard.jsx`: prop-driven (name, tagline, color accent, icon, route)
- [ ] Router: `/` → Dashboard, `/homematch`, `/futurepath`, `/kindconnect`, `/firststep`
- [ ] `SubProjectLanding.jsx`: hero with sub-project name, one-paragraph description, prominent "Speak with [developer name]" card, "← Back to Community Compass" link

**Sub-project page content:**

| Slug | Name | Developer | One-liner |
|---|---|---|---|
| homematch | HomeMatch | Niciah Rymer-Hillian | Personalized housing navigation with eligibility-based matching, interactive maps, and an AI Housing Assistant. |
| futurepath | FuturePath | Shocka Holmes | AI-assisted intake and structured guidance for young adults transitioning out of foster care. |
| kindconnect | Kind Connect | Prachi Patel | Community support and well-being resource discovery, wellness check-ins, and a volunteer network. |
| firststep | First Step | Anitra Johnson | Community resources, policy updates, and news for everyone. |

**Demo state:** Visitor lands on Community Compass, sees four cards, clicks each, sees branded landing page with "speak with [name]" CTA. Zero chat yet.

**Work split idea:** Partner builds the cards and pages (visual-heavy), you build the layout + routing.

---

### M2 — CompassChat slide-out spine, mocked data (2 days)

- [ ] `ChatTrigger.jsx`: floating button bottom-right, mint background, chat icon, yellow "1" badge
- [ ] `ChatPanel.jsx`: slide-in from right, 420px wide, mint accents, backdrop dim
- [ ] `Sidebar.jsx`: workspace name top, "AI Assistant" pinned at top with star icon, "Channels" header + list, "Direct messages" header + list
- [ ] `MessageThread.jsx`: avatar + name + timestamp + content + Done/Like/Seen pills (visual only)
- [ ] `Composer.jsx`: simplified rich-text toolbar (B / I / Link visual only), textarea, mint Send button
- [ ] `DemoBanner.jsx`: yellow banner — "Demo mode — messages persist to the backend"
- [ ] Wire everything to `useChatStore` with **hardcoded mock data** (3 channels, 2 DMs, 10 messages in one channel)

**Demo state:** Click FAB → panel slides in → see channels, click one → see messages → can type and send (locally only, no backend yet).

**Work split idea:** Partner does message thread + composer (she's already designed these), you do sidebar + panel mechanics.

---

### M3 — Backend wiring (1.5 days)

- [ ] `lib/auth.js`: on boot, check for stored JWT; if none, POST to `/api/auth/login` with seeded demo user credentials, store token
- [ ] `lib/api.js`: fetch wrapper that attaches `Authorization: Bearer <token>` automatically
- [ ] `lib/websocket.js`: STOMP client, connects to `/ws` with JWT in CONNECT header
- [ ] Wire `Sidebar.jsx`: load channels from `GET /api/channels/mine`
- [ ] Wire `MessageThread.jsx`: load history from `GET /api/channels/{id}/messages?page=0&size=50`
- [ ] On channel selection: subscribe to `/topic/channels/{id}`, append incoming messages
- [ ] Wire `Composer.jsx`: publish to `/app/channels/{id}/messages`
- [ ] Subscribe to `/user/queue/errors` → show as Toast
- [ ] Backend ask: add a demo-user seeder to `CompassChatApplication.java` if not already there

**Demo state:** Open two browser tabs side by side, type in one, message appears in both. Messages persist across reloads. (This is the "wow" moment for the demo.)

**Work split idea:** You handle backend integration (you wrote it), partner polishes the chat UI.

---

### M4 — AI Assistant with intent routing (1.5 days)

- [ ] `intentRouter.js`: classify a typed message
  - **Urgent intent:** keywords like "help me", "human", "emergency", "real person", "talk to someone" → return `{intent: 'URGENT'}`
  - **Mood intent:** keywords from your MoodType enum — "sad", "anxious", "overwhelmed", "stressed", "happy", "calm", "lonely", "tired", etc. → return `{intent: 'MOOD', moodType, note}`
  - **Sub-project intent:** keywords mapped to sub-projects (housing/rent → HomeMatch, foster/youth → FuturePath, volunteer/wellness → KindConnect, news/policy → FirstStep) → return `{intent: 'RESOURCE', subProject}`
  - **Default:** `{intent: 'GENERAL'}` — polite acknowledgment + escalate offer
- [ ] `AIConversation.jsx`: specialized message thread; user messages on right, AI on left
- [ ] AI message types: text, `SmartSuggestion`, `ResourceCard`, `CrisisBlock`, `EscalateCTA`
- [ ] `SmartSuggestion.jsx`: yellow-tinted card with title + body + optional action button (lifted from your partner's design)
- [ ] **Mood flow:** detect mood → POST to `/api/moods` → render AI reply with `MoodResponse.data.resources` as `ResourceCard`s. If `distressed`, render `CrisisBlock` first (your backend already prepends these, just render in order).
- [ ] **Resource flow:** match sub-project → respond with one-liner + `ResourceCard` linking to that sub-project's landing page
- [ ] **Urgent flow:** respond with empathy message + `EscalateCTA` button → on click, switch active conversation to a moderator DM with pre-filled context
- [ ] **First-visit greeting:** on initial load, seed `useAIStore` with an AI welcome message; FAB badge shows "1"

**Demo state:** Type "I'm feeling overwhelmed" into AI Assistant. AI replies with empathy + crisis resources first + Kind Connect resource card. Click the resource card → opens Kind Connect landing page. Type "I need help finding housing" → AI suggests HomeMatch. Type "I want to talk to a real person" → AI offers escalation, click → switches to Admin Team conversation.

**Work split idea:** You build intent router + mood integration (backend logic). Partner builds AI message component variants (visual).

---

### M5 — Polish + demo data + edge cases (1 day)

- [ ] Seed backend with rich demo data: 2-3 channels with realistic message history, 1-2 DM threads, the demo user already a member
- [ ] Loading states for every async call (skeleton bubbles in thread, spinner in sidebar)
- [ ] Empty states: "No messages yet — say hi 👋"
- [ ] Slide-in animation timing: ~250ms ease-out
- [ ] FAB unread badge: yellow, pulses gently on first load
- [ ] Sound cue (optional, low priority): subtle ping when a new message arrives while panel is closed
- [ ] Mobile: at <768px the slide-out becomes full-screen; on small viewports the FAB is bottom-center
- [ ] Accessibility pass: keyboard navigation through channels (↑↓), focus trap in panel, ARIA labels on FAB and Send
- [ ] Final logo lockup tested at favicon size

**Demo state:** Anyone can land on the site cold, follow the script below, and finish thinking "I want to hire these people."

---

## 5. Backend endpoint map

| UI action | Backend call | Notes |
|---|---|---|
| App boot | `POST /api/auth/login` (demo creds) | Auto-login; stash JWT |
| Open chat | `GET /api/channels/mine` | Populates Sidebar |
| Select channel | `GET /api/channels/{id}/messages?page=0&size=50` | Newest first; reverse for display |
| Live updates | STOMP subscribe `/topic/channels/{id}` | Append on receive |
| Send message | STOMP publish `/app/channels/{id}/messages` | Server broadcasts back |
| AI: mood detected | `POST /api/moods` | Returns `MoodResponse` with resources |
| AI: resource detected | (none) | Use static `resources.json` |
| AI: urgent detected | (none) | Frontend switches conversation locally |
| Sub-project click | (none) | Client-side routing |
| Errors | STOMP subscribe `/user/queue/errors` | Toast notification |

---

## 6. Demo script (memorize this for the presentation)

1. Land on Community Compass dashboard. Show the four sub-project cards.
2. Click HomeMatch → quick look at landing page → "Back to Community Compass."
3. Click the floating chat icon. Show the slide-out animation.
4. Show the channel list. Click a channel with mock conversation. Show that the same conversation in a second tab updates live as you type.
5. Switch to AI Assistant. Type: **"I'm feeling overwhelmed and need help finding housing."**
6. AI responds: empathy message → crisis resource block first (because "overwhelmed" is distressed) → HomeMatch resource card → "Would you like to talk to someone?" CTA.
7. Click the HomeMatch card → lands on the sub-project page → "Speak with Niciah Rymer-Hillian" displayed.
8. Back to chat. Type: **"I need to talk to a human."**
9. AI responds: handoff message → click → conversation switches to Admin Team channel with a pre-filled context message.

That's the demo. Three minutes, hits every feature.

---

## 7. Risks and tradeoffs (the honest section)

- **The biggest risk is M3 (WebSocket integration).** STOMP setup with JWT is finicky; budget extra time. If you hit a wall, the demo can survive on REST-only by polling history every 2 seconds — ugly but works. Don't waste a whole day on a WebSocket bug if you have a deadline.
- **The intent router is dumb keyword matching.** That's fine. If a reviewer asks, the honest answer is: "We knew the AI would be the demo's weakest point if implemented client-side. We scoped it that way deliberately so the rest of the system could be solid. A production version would call our integration package, which would route through Anthropic's API with proper intent classification." That's a senior answer.
- **No login means no real multi-user testing.** The demo user is alone in the world. If you can spare half a day in M5, seed a second user named "Case Worker" and pre-populate a DM thread so the conversation feels alive.
- **Done/Like/Seen pills are visual only.** If anyone asks: "Mock data for demonstration; reactions are on the v1.1 roadmap." Don't pretend they work.

---

## 8. What's not in this plan (intentionally)

- Notification panel (the bell icon and unread feed) — defer to v1.1
- Mention parsing in the UI (@username highlighting beyond visual styling) — defer
- File uploads / attachments
- Threading (replies to a message)
- Search
- Reminders UI (despite the backend supporting them — out of scope for this demo)
- Real admin/moderator tools
- Mobile-first responsive (light pass only in M5)
- E2E tests (Jest unit tests for `intentRouter.js` are worth writing because that file is pure logic and will be the only meaningful frontend test target)

---

## 9. Definition of done

- All five milestones complete
- The demo script above executes cleanly without console errors
- Tested in Chrome, Firefox, and Safari
- Two browser tabs side by side show live messaging
- The crisis safety net resources appear when distressed mood keywords are used
- README.md updated with: how to run, the demo user credentials, and a one-paragraph project summary

Ship it.

---
---

# Plan: Make CompassChat Deployable (Portable Docker + Postgres for Prod)

> Appended post-demo. Roadmap Phase 1 (deployment). This is a planning document — not yet implemented.

## Context

CompassChat currently runs only on a developer laptop (backend `mvn spring-boot:run` on 8081, frontend `vite` on 5173, H2 in-memory). Roadmap Phase 1 = get it deployable. The goal here is a **portable Docker setup** that runs on any host with Docker — not tied to one cloud.

### Confirmed decisions
- **Portable Docker (any host):** Dockerfiles + a working `docker-compose.yml` (backend + frontend nginx + Postgres). No cloud-specific blueprint.
- **Postgres for prod, H2 for local/test:** a Spring `prod` profile uses Postgres from env vars; default profile keeps H2 (local dev + the 51-class test suite unchanged).
- **Keep seeding + auto-login:** the deployed link "just works" — demo data seeds on first boot, frontend auto-logs in as `demo-resident`.

### Key facts verified
- Backend: Java 17, Spring Boot 3.3.5, Maven fat jar `target/java-service-0.1.0.jar`, **no Maven wrapper**, `spring-boot-maven-plugin` present.
- `application.properties`: `server.port=8081`, H2 `create-drop`, `app.jwt.secret` **hardcoded**, `GROQ_API_KEY` already env-driven. The `spring.ai.*` Groq autoconfig-exclude block is delicate — **must be preserved verbatim**.
- H2 dependency is `runtime` scope (keep). Need to add the PostgreSQL driver.
- Both seeders are **idempotent** (`DemoDataSeeder` guards on `findByUsername("demo-resident")`; `DataSeeder` guards on `existsByName`) → safe on persistent Postgres across restarts; `ddl-auto=update` won't duplicate data.
- Frontend talks to backend via **relative `/api` + `/ws`** (`api.js` `BASE_URL='/api'`, `websocket.js` `new SockJS('/ws')`) — no hardcoded host → nginx reverse proxy is the prod model. Vite proxy is dev-only.
- Security: no CORS bean; `/ws/**`, `/api/auth/**`, `/api/ai/**`, `/h2-console/**` are permitAll. (Same-origin via nginx → no CORS needed in prod.)
- WebSocket endpoint is registered `withSockJS()` (fixed earlier) — nginx must pass `/ws` with HTTP/1.1 upgrade headers.

---

## 1. Backend: prod profile + Postgres + `$PORT` + externalized secret

**File:** `backend/src/main/resources/application.properties`
- Change `server.port=8081` → `server.port=${PORT:8081}` (host can inject a port; defaults to 8081).
- Change `app.jwt.secret=...` → `app.jwt.secret=${APP_JWT_SECRET:CompassChatSuperSecretKeyForDevelopmentOnly1234567890}` (env override in prod; dev fallback unchanged).
- Leave H2 datasource, the Groq block, and the autoconfigure-exclude **exactly as-is** (default profile = local dev/test).

**File (new):** `backend/src/main/resources/application-prod.properties`
- Postgres datasource from env:
  ```
  spring.datasource.url=${DB_URL}
  spring.datasource.username=${DB_USER}
  spring.datasource.password=${DB_PASSWORD}
  spring.datasource.driver-class-name=org.postgresql.Driver
  spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
  spring.jpa.hibernate.ddl-auto=update
  spring.h2.console.enabled=false
  ```
  (Activated by `SPRING_PROFILES_ACTIVE=prod`. Data persists; idempotent seeders won't duplicate.)

**File:** `backend/pom.xml`
- Add the PostgreSQL driver dependency (runtime scope):
  ```xml
  <dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
  </dependency>
  ```
- (Optional, recommended) add `spring-boot-starter-actuator` for a `/actuator/health` healthcheck used by compose. If added, permit `/actuator/health` in `SecurityConfig` (add `.requestMatchers("/actuator/health").permitAll()`).

## 2. Backend Dockerfile (multi-stage)

**File (new):** `backend/Dockerfile`
- Stage 1 `maven:3.9-eclipse-temurin-17`: copy `pom.xml`, `mvn -q dependency:go-offline`, copy `src/`, `mvn -q clean package -DskipTests` → `target/java-service-0.1.0.jar`.
- Stage 2 `eclipse-temurin:17-jre`: copy the jar → `/app/app.jar`, `EXPOSE 8081`, `ENTRYPOINT ["java","-jar","/app/app.jar"]`. Reads `PORT`, `SPRING_PROFILES_ACTIVE`, `DB_*`, `APP_JWT_SECRET`, `GROQ_API_KEY` at runtime.

**File (new):** `backend/.dockerignore` → `target/`, `.idea/`, `*.iml`, `.git/`.

## 3. Frontend Dockerfile (build → nginx) + reverse proxy

**File (new):** `frontend/Dockerfile`
- Stage 1 `node:20-alpine`: copy `package*.json`, `npm ci`, copy rest, `npm run build` → `dist/`.
- Stage 2 `nginx:1.27-alpine`: copy `dist/` → `/usr/share/nginx/html`, copy nginx config; `EXPOSE 8080`.

**File (new):** `frontend/nginx.conf`
- `listen 8080;`, `root /usr/share/nginx/html; index index.html;`
- SPA fallback: `location / { try_files $uri /index.html; }`
- `location /api/ { proxy_pass http://backend:8081; proxy_set_header Host $host; ... }`
- `location /ws/ { proxy_pass http://backend:8081; proxy_http_version 1.1; proxy_set_header Upgrade $http_upgrade; proxy_set_header Connection "upgrade"; proxy_read_timeout 1h; }` (SockJS/STOMP needs the upgrade headers + long timeout)

**File (new):** `frontend/.dockerignore` → `node_modules/`, `dist/`, `.git/`.

> The `backend` hostname in nginx resolves over the compose network (service name). On another host/orchestrator, point it at the backend's URL.

## 4. docker-compose.yml (the deployable unit)

**File:** `docker-compose.yml` (currently empty)
```yaml
services:
  db:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: compasschat
      POSTGRES_USER: compass
      POSTGRES_PASSWORD: ${DB_PASSWORD:-compass-dev-pw}
    volumes: [ "pgdata:/var/lib/postgresql/data" ]
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U compass -d compasschat"]
      interval: 5s
      timeout: 5s
      retries: 10
  backend:
    build: ./backend
    environment:
      PORT: 8081
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:postgresql://db:5432/compasschat
      DB_USER: compass
      DB_PASSWORD: ${DB_PASSWORD:-compass-dev-pw}
      APP_JWT_SECRET: ${APP_JWT_SECRET:-change-me-in-prod}
      GROQ_API_KEY: ${GROQ_API_KEY:-}
    depends_on:
      db: { condition: service_healthy }
    expose: [ "8081" ]
  frontend:
    build: ./frontend
    ports: [ "8080:8080" ]
    depends_on: [ backend ]
volumes: { pgdata: {} }
```
Run: `docker compose up --build` → open `http://localhost:8080`. Postgres persists in the `pgdata` volume; seeders populate on first boot only.

## 5. Env template + docs

**File (new):** `.env.example`
```
DB_PASSWORD=compass-dev-pw
APP_JWT_SECRET=change-me-to-a-long-random-string
GROQ_API_KEY=         # optional; empty = rule-based AI fallback
```
**File:** append `.env` to `.gitignore`.
**File:** `README.md` — add a "Run with Docker" section (the 3 env vars, `docker compose up --build`, the `http://localhost:8080` URL, note that without `GROQ_API_KEY` the AI uses the rule-based fallback, and that data now persists in Postgres).

---

## Files to Create / Modify

| File | Change |
|---|---|
| `backend/src/main/resources/application.properties` | `server.port=${PORT:8081}`; `app.jwt.secret=${APP_JWT_SECRET:...}`. Leave H2 + Groq blocks intact. |
| `backend/src/main/resources/application-prod.properties` | New — Postgres datasource from `DB_*` env, `ddl-auto=update`, H2 console off. |
| `backend/pom.xml` | Add `postgresql` (runtime); optionally `spring-boot-starter-actuator`. |
| `backend/src/main/java/.../auth/security/SecurityConfig.java` | (Only if actuator added) permit `/actuator/health`. |
| `backend/Dockerfile`, `backend/.dockerignore` | New — multi-stage Maven build → JRE runtime. |
| `frontend/Dockerfile`, `frontend/.dockerignore`, `frontend/nginx.conf` | New — Vite build → nginx serving static + proxying `/api` and `/ws` (WebSocket upgrade). |
| `docker-compose.yml` | Fill in db + backend + frontend (was empty). |
| `.env.example`, `.gitignore`, `README.md` | New env template; ignore `.env`; Docker run docs. |

No application/business-logic changes. Local dev (`mvn spring-boot:run` + `vite`) and the test suite keep using H2 unchanged (default profile).

---

## Verification

1. **Local containerized run:** from repo root, `cp .env.example .env`, then `docker compose up --build`.
2. Postgres becomes healthy; backend starts with `prod` profile and logs `>>> Seeding CompassChat demo data...` (first run) then connects to Postgres.
3. Open `http://localhost:8080` → app loads, auto-logs in as demo-resident; sidebar shows channels + DMs.
4. Network tab: `GET /api/channels` → 200 (nginx → backend); `/ws` SockJS connects (`[ws] connected`) → live messages, Admin Team scripted demo, and Sage mood flow all work.
5. AI: with no `GROQ_API_KEY`, responses come from the rule-based fallback; set `GROQ_API_KEY` in `.env` and re-up → Groq responses.
6. **Persistence test:** post a message / log in, then `docker compose restart backend` → data still present (Postgres volume), and logs show `>>> Demo data already seeded, skipping.` (idempotent seeders).
7. (If actuator added) `curl http://localhost:8081/actuator/health` via the backend container → `{"status":"UP"}`.
8. Tests still pass on H2: `cd backend && mvn test` (default profile, no Docker) → green.
9. Tear down: `docker compose down` (keeps volume) / `docker compose down -v` (wipes Postgres).

> Portability note: this compose file runs on any Docker host (local, a VM, a server). To later put it on a specific PaaS (e.g. Render), the same Dockerfiles are reused — only a platform blueprint + `$PORT`/managed-Postgres wiring would be added; nothing here blocks that.
