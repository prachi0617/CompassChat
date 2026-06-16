# Test coverage (80%) plan

## Step 1 — Enable coverage reporting + enforcement
- [x] Edit pom.xml to add JaCoCo plugin
- [ ] Configure report output + coverage exclusions (if needed)
- [x] Enforce minimum total coverage >= 80%

## Step 2 — Scaffold test infrastructure
- [ ] Create src/test/java/com/compasschat/testutils helpers (fixtures/builders)
- [ ] Add base JUnit 5 + Mockito setup
- [ ] Add test-specific Spring properties if required

## Step 3 — Add coverage tests module-by-module
- [ ] common/base + GlobalExceptionHandler tests
- [ ] auth (JwtService/JwtAuthFilter/AuthService) tests
- [ ] user + repositories/service tests
- [ ] channel/member/role tests
- [ ] mention/message/mood/reminder/notification/volunteer/attachment tests
- [ ] websocket tests (presence/message listeners/controllers)

## Step 4 — Run and iterate
- [ ] Run mvn test
- [ ] Run mvn jacoco:report
- [ ] Inspect HTML report for misses
- [ ] Add targeted tests for remaining uncovered branches/lines

## Step 5 — Confirm requirement
- [ ] Verify JaCoCo total coverage >= 80%
- [ ] Ensure build fails if threshold not met

