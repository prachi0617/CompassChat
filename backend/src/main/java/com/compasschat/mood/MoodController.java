package com.compasschat.mood;

import com.compasschat.auth.security.JwtService;
import com.compasschat.common.base.ApiResponse;
import com.compasschat.mood.dto.CreateMoodRequest;
import com.compasschat.mood.dto.MoodHistoryEntry;
import com.compasschat.mood.dto.MoodResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    private final MoodService moodService;
    private final JwtService jwtService;

    public MoodController(MoodService moodService, JwtService jwtService) {
        this.moodService = moodService;
        this.jwtService = jwtService;
    }

    /** Log a mood and get back the entry + recommended resources. */
    @PostMapping
    public ResponseEntity<ApiResponse<MoodResponse>> log(
            @Valid @RequestBody CreateMoodRequest req,
            HttpServletRequest http) {

        UUID userId = currentUserId(http);
        MoodService.MoodLogResult result =
                moodService.logMood(userId, req.moodType(), req.note());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Mood recorded",
                        MoodResponse.from(result.mood(), result.resources())));
    }

    /** My history, paged. URL is /me to make "scope is self" obvious. */
    @GetMapping("/me")
    public ApiResponse<Page<MoodHistoryEntry>> myHistory(
            HttpServletRequest http,
            Pageable pageable) {

        UUID userId = currentUserId(http);
        Page<MoodHistoryEntry> page = moodService
                .getHistoryForUser(userId, userId, pageable)
                .map(MoodHistoryEntry::from);
        return ApiResponse.ok(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<MoodHistoryEntry> getOne(
            @PathVariable UUID id,
            HttpServletRequest http) {

        return ApiResponse.ok(MoodHistoryEntry.from(
                moodService.getOneForUser(id, currentUserId(http))));
    }

    // ----- JWT helpers -----

    private UUID currentUserId(HttpServletRequest http) {
        return jwtService.getUserId(extractToken(http));
    }

    private String extractToken(HttpServletRequest http) {
        String header = http.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing token");
        }
        return header.substring(7);
    }
}
