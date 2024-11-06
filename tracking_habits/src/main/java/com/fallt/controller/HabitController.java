package com.fallt.controller;

import com.fallt.dto.request.HabitConfirmRequest;
import com.fallt.dto.request.UpsertHabitRequest;
import com.fallt.dto.response.HabitExecutionResponse;
import com.fallt.dto.response.HabitResponse;
import com.fallt.exception.ExceptionResponse;
import com.fallt.security.AuthenticationContext;
import com.fallt.service.HabitService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
@Tag(name = "Контроллер управления привычками")
public class HabitController {

    private final HabitService habitService;
    private final AuthenticationContext authenticationContext;
    private final SessionUtils sessionUtils;
    private final ValidationService validationService;

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
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return ResponseEntity.ok(habitService.getAllHabits(userEmail));
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
    public ResponseEntity<HabitResponse> createHabit(@RequestBody UpsertHabitRequest request) {
        validationService.checkUpsertHabitRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.saveHabit(userEmail, request));
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
        validationService.checkHabitConfirmRequest(request);
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitService.confirmHabit(userEmail, request));
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
    @PutMapping
    public ResponseEntity<HabitResponse> updateHabit(@RequestParam(name = "title") String title, @RequestBody UpsertHabitRequest request) {
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        return ResponseEntity.ok(habitService.updateHabit(userEmail, title, request));
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
    @DeleteMapping
    public ResponseEntity<Void> deleteHabit(@RequestParam(name = "title") String title) {
        String userEmail = authenticationContext.getEmailCurrentUser(sessionUtils.getSessionIdFromContext());
        habitService.deleteHabit(userEmail, title);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
