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

    @Operation(
            summary = "Получение списка привычек",
            description = "Возвращает список привычек пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получение списка привычек", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = HabitResponse.class)), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @GetMapping
    public ResponseEntity<List<HabitResponse>> getAllHabits() {
        return ResponseEntity.ok(habitService.getAllHabits());
    }

    @Operation(
            summary = "Добавление новой привычки",
            description = "Добавляет новую привычку пользователю"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Добавление привычки", content = {
                    @Content(schema = @Schema(implementation = HabitResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание title уже имеющейся привычки", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/create")
    public ResponseEntity<HabitResponse> createHabit(@RequestBody @Valid UpsertHabitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.saveHabit(request));
    }

    @Operation(
            summary = "Подтверждение выполнения привычки",
            description = "Отмечает выполнение привычки в определенную дату"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Подтверждение выполнения привычки", content = {
                    @Content(schema = @Schema(implementation = HabitExecutionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание title уже имеющейся привычки", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PostMapping("/confirm")
    public ResponseEntity<HabitExecutionResponse> confirmHabitExecution(@RequestBody HabitConfirmRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.confirmHabit(request));
    }

    @Operation(
            summary = "Обновление привычки",
            description = "Обновляет данные о привычке"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновление привычки", content = {
                    @Content(schema = @Schema(implementation = HabitResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание невалидных данных", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "400", description = "Указание title уже имеющейся привычки", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            }),
            @ApiResponse(responseCode = "404", description = "Указание отсутствующей для обновления привычки", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponse> updateHabit(@PathVariable Long id, @RequestBody UpsertHabitRequest request) {
        return ResponseEntity.ok(habitService.updateHabit(id, request));
    }

    @Operation(
            summary = "Удаление привычки",
            description = "Удаление привычки"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Удаление привычки"),
            @ApiResponse(responseCode = "401", description = "Запрос от неаутентифицированного пользователя", content = {
                    @Content(schema = @Schema(implementation = ExceptionResponse.class), mediaType = "application/json")
            })
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable Long id) {
        habitService.deleteHabit(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
