package com.fallt.controller;

import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.response.ExceptionResponse;
import com.fallt.domain.dto.response.ExecutionDto;
import com.fallt.domain.dto.response.HabitProgress;
import com.fallt.service.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @Operation(
            summary = "Получение полного отчета"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = HabitProgress.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @GetMapping("/full")
    public ResponseEntity<HabitProgress> getFullProgress(@RequestBody @Valid ReportRequest request) {
        return ResponseEntity.ok(statisticService.getHabitProgress(request));
    }

    @Operation(
            summary = "Получение серии выполнения привычки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = ExecutionDto.class)))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @GetMapping("/streak")
    public ResponseEntity<List<ExecutionDto>> getStreak(@RequestBody @Valid ReportRequest request) {
        return ResponseEntity.ok(statisticService.getHabitStreak(request));
    }

}
