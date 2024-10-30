package com.fallt.controller;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.dto.response.HabitResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.HabitService;
import com.fallt.service.ValidationService;
import com.fallt.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    private final AuthenticationContext authenticationContext;

    private final SessionUtils sessionUtils;

    private final ValidationService validationService;

    @GetMapping
    public List<HabitResponse> getAllHabits() {
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return habitService.getAllHabits(userEmail);
    }

    @PostMapping("/create")
    public HabitResponse createHabit(@RequestBody UpsertHabitRequest request) {
        validationService.checkUpsertHabitRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return habitService.saveHabit(userEmail, request);
    }

    @PostMapping("/confirm")
    public HabitExecutionResponse confirmHabitExecution(@RequestBody HabitConfirmRequest request) {
        validationService.checkHabitConfirmRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return habitService.confirmHabit(userEmail, request);
    }

    @PutMapping
    public HabitResponse updateHabit(@RequestParam(name = "title") String title, @RequestBody UpsertHabitRequest request) {
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return habitService.updateHabit(userEmail, title, request);
    }

    @DeleteMapping
    public void deleteHabit(@RequestParam(name = "title") String title) {
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        habitService.deleteHabit(userEmail, title);
    }
}
