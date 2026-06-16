# TODO - Frontend review (UI) wired to backend

## Step 1: Plan
- [x] Inspect existing frontend pages/components
- [x] Inspect backend auth/channel/message REST APIs + websocket destinations
- [ ] Create edit plan and get approval

## Step 2: Implement frontend API wiring
- [ ] Add API client module for REST calls + auth token storage
- [ ] Add auth page (login/register) calling `/api/auth/*`
- [ ] Add chat UI: list channels `/api/channels/mine`, load messages, send via websocket

## Step 3: Implement websocket real-time messaging
- [ ] Use STOMP client to connect to `ws://<backend>/ws`
- [ ] Subscribe to `/topic/channels/{channelId}` and append incoming messages
- [ ] Handle errors from `/user/queue/errors`

## Step 4: Run & verify
- [ ] Start backend + frontend
- [ ] Smoke test: register/login -> see channels -> send/edit/delete if implemented
- [ ] Fix CORS/config issues if any

