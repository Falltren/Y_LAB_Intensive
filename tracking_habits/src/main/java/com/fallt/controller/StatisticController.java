package com.fallt.controller;

import com.fallt.dto.request.ReportRequest;
import com.fallt.dto.response.ExecutionDto;
import com.fallt.dto.response.HabitProgress;
import com.fallt.exception.ExceptionResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.StatisticService;
import com.fallt.service.impl.ValidationService;
import com.fallt.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Контроллер получения отчетов")
public class StatisticController {

    private final StatisticService statisticService;

    private final SessionUtils sessionUtils;

    private final ValidationService validationService;

    private final AuthenticationContext authenticationContext;

    @Operation(
            summary = "Получение полного отчета",
            description = "Предоставляет отчет содержащий различные метрики по привычкам"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение полного отчета", content = {
                    @Content(schema = @Schema(implementation = HabitProgress.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/full")
    public HabitProgress getFullProgress(@RequestBody ReportRequest request) {
        validationService.checkReportRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return statisticService.getHabitProgress(userEmail, request);
    }

    @Operation(
            summary = "Получение серии выполнения привычки",
            description = "Предоставляет отчет, содержащий данные по выполнению привычки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение отчета", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ExecutionDto.class)), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping("/streak")
    public List<ExecutionDto> getStreak(@RequestBody ReportRequest request) {
        validationService.checkReportRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return statisticService.getHabitStreak(userEmail, request);
    }
}
