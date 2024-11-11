package com.fallt.controller;

import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.response.ExceptionResponse;
import com.fallt.domain.dto.response.HabitExecutionResponse;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.service.HabitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
@Tag(name = "Контроллер управления привычками")
public class HabitController {

    private final HabitService habitService;

    @Operation(summary = "Получение списка привычек")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = HabitResponse.class)))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits() {
        return ResponseEntity.ok(habitService.getAllHabits());
    }

    @Operation(summary = "Добавление новой привычки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = HabitResponse.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @PostMapping("/create")
    public ResponseEntity<HabitResponse> createHabit(@RequestBody @Valid UpsertHabitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.saveHabit(request));
    }

    @Operation(summary = "Подтверждение выполнения привычки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = HabitExecutionResponse.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @PostMapping("/confirm")
    public ResponseEntity<HabitExecutionResponse> confirmHabitExecution(@RequestBody @Valid HabitConfirmRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.confirmHabit(request));
    }

    @Operation(summary = "Обновление привычки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = HabitResponse.class))}),
            @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(@PathVariable Long id, @RequestBody UpsertHabitRequest request) {
        return ResponseEntity.ok(habitService.updateHabit(id, request));
    }

    @Operation(summary = "Удаление привычки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "401", content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
