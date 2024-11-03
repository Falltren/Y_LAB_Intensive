package com.fallt.service;

import com.fallt.aop.audit.ActionType;
import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.dto.response.ExecutionDto;
import com.fallt.dto.response.HabitProgress;
import com.fallt.dto.request.ReportRequest;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для расчета различной статистики по выполнению привычек пользователем
 */
@RequiredArgsConstructor
@Loggable
public class StatisticService {

    private final HabitService habitService;

    private final UserService userService;

    /**
     * Получение общей статистики по выполнению привычки пользователем, включая название привычки,
     * % успешного выполнения и серии выполнения
     *
     * @param userEmail Электронный адрес пользователя
     * @param request   Объект, содержащий данные о названии привычки, а также дате начала и окончания отчетного периода
     * @return Прогресс выполнения пользователем привычки
     */
    @Auditable(action = ActionType.GET)
    public HabitProgress getHabitProgress(String userEmail, ReportRequest request) {
        User user = userService.getUserByEmail(userEmail);
        Habit habit = habitService.getHabitByTitle(user, request.getTitle());
        HabitProgress progress = new HabitProgress();
        progress.setTitle(habit.getTitle());
        List<ExecutionDto> executions = getHabitStreak(userEmail, request);
        progress.setSuccessRate(calculateSuccessRate(executions));
        progress.setExecution(executions);
        return progress;
    }

    /**
     * Расчет % успешного выполнения привычки за указанный период
     *
     * @param userEmail Электронный адрес пользователя
     * @param request   Объект, содержащий данные о названии привычки, а также дате начала и окончания отчетного периода
     * @return % успешного выполнения привычки
     */
    public int getSuccessHabitRate(String userEmail, ReportRequest request) {
        List<ExecutionDto> executions = getHabitStreak(userEmail, request);
        return calculateSuccessRate(executions);
    }

    /**
     * Расчет серии выполнения привычки
     *
     * @param userEmail Электронный адрес пользователя
     * @param request   Объект, содержащий данные о названии привычки, а также дате начала и окончания отчетного периода
     * @return Список с данными по выполнению привычки за указанны период
     */
    @Auditable(action = ActionType.GET)
    public List<ExecutionDto> getHabitStreak(String userEmail, ReportRequest request) {
        User user = userService.getUserByEmail(userEmail);
        Habit habit = habitService.getHabitByTitle(user, request.getTitle());
        return switch (habit.getExecutionRate()) {
            case DAILY -> getDailyHabitStreak(habit, request.getStart(), request.getEnd());
            case WEEKLY -> getWeeklyHabitStreak(habit, request.getStart(), request.getEnd());
            case MONTHLY -> getMonthlyHabitStreak(habit, request.getStart(), request.getEnd());
        };
    }

    private int calculateSuccessRate(List<ExecutionDto> executions) {
        long success = executions.stream().filter(ExecutionDto::isExecuted).count();
        return Math.round((float) success * 100 / executions.size());
    }

    private List<ExecutionDto> getDailyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<ExecutionDto> result = new ArrayList<>();
        LocalDate currentDay = start;
        while (!currentDay.isAfter(end)) {
            if (habit.getSuccessfulExecution().contains(currentDay)) {
                result.add(new ExecutionDto(currentDay, currentDay, true));
            } else {
                result.add(new ExecutionDto(currentDay, currentDay, false));
            }
            currentDay = currentDay.plusDays(1);
        }
        return result;
    }

    private List<ExecutionDto> getWeeklyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<LocalDate> executed = new ArrayList<>(habit.getSuccessfulExecution());
        List<ExecutionDto> result = new ArrayList<>();
        int dateOfWeek = start.getDayOfWeek().getValue();
        LocalDate startOfWeek = start;
        LocalDate endOfWeek = start.plusDays((long) 7 - dateOfWeek);
        while (!endOfWeek.isAfter(end)) {
            if (executed.isEmpty()) {
                result.add(new ExecutionDto(startOfWeek, endOfWeek, false));
            } else if (!executed.get(0).isBefore(startOfWeek) && !executed.get(0).isAfter(endOfWeek)) {
                result.add(new ExecutionDto(startOfWeek, endOfWeek, true));
                executed.remove(0);
            } else {
                result.add(new ExecutionDto(startOfWeek, endOfWeek, false));
            }
            startOfWeek = endOfWeek.plusDays(1);
            endOfWeek = endOfWeek.plusWeeks(1);
            if (endOfWeek.isAfter(end) && startOfWeek.isBefore(end) && !executed.isEmpty()) {
                result.add(new ExecutionDto(startOfWeek, end, true));
            } else if (endOfWeek.isAfter(end) && startOfWeek.isBefore(end) && executed.isEmpty()) {
                result.add(new ExecutionDto(startOfWeek, end, false));
            }
        }
        return result;
    }

    private List<ExecutionDto> getMonthlyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<LocalDate> executed = new ArrayList<>(habit.getSuccessfulExecution());
        List<ExecutionDto> result = new ArrayList<>();
        LocalDate startOfMonth = start;
        LocalDate endOfMonth = start.withDayOfMonth(start.getMonth().length(start.isLeapYear()));
        while (!endOfMonth.isAfter(end)) {
            if (executed.isEmpty()) {
                result.add(new ExecutionDto(startOfMonth, endOfMonth, false));
            } else if (!executed.get(0).isBefore(startOfMonth) && !executed.get(0).isAfter(endOfMonth)) {
                result.add(new ExecutionDto(startOfMonth, endOfMonth, true));
                executed.remove(0);
            } else {
                result.add(new ExecutionDto(startOfMonth, endOfMonth, false));
            }
            startOfMonth = endOfMonth.plusDays(1);
            endOfMonth = endOfMonth.plusMonths(1);
            if (endOfMonth.isAfter(end) && startOfMonth.isBefore(end) && !executed.isEmpty()) {
                result.add(new ExecutionDto(startOfMonth, end, true));
            } else if (endOfMonth.isAfter(end) && startOfMonth.isBefore(end) && executed.isEmpty()) {
                result.add(new ExecutionDto(startOfMonth, end, false));
            }
        }
        return result;
    }
}
