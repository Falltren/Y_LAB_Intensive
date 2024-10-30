package com.fallt.controller;

import com.fallt.dto.request.ReportRequest;
import com.fallt.dto.response.HabitProgress;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.StatisticService;
import com.fallt.service.ValidationService;
import com.fallt.util.SessionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    private final SessionUtils sessionUtils;

    private final ValidationService validationService;

    private final AuthenticationContext authenticationContext;

    @GetMapping("/full")
    public HabitProgress getFullProgress(@RequestBody ReportRequest request) {
        validationService.checkReportRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return statisticService.getHabitProgress(userEmail, request);
    }
}
