package com.fallt.service.impl;

import com.fallt.audit_starter.aop.Auditable;
import com.fallt.audit_starter.domain.entity.enums.ActionType;
import com.fallt.domain.dto.request.ReportRequest;
import com.fallt.domain.dto.response.ExecutionDto;
import com.fallt.domain.dto.response.HabitProgress;
import com.fallt.domain.entity.Habit;
import com.fallt.logging.annotation.Loggable;
import com.fallt.service.HabitService;
import com.fallt.service.StatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Loggable
@Service
public class StatisticServiceImpl implements StatisticService {

    private final HabitService habitService;

    @Auditable(action = ActionType.GET)
    public HabitProgress getHabitProgress(ReportRequest request) {
        Habit habit = habitService.getHabitById(request.getHabitId());
        HabitProgress progress = new HabitProgress();
        progress.setTitle(habit.getTitle());
        List<ExecutionDto> executions = getHabitStreak(request);
        progress.setSuccessRate(calculateSuccessRate(executions));
        progress.setExecution(executions);
        return progress;
    }

    public int getSuccessHabitRate(ReportRequest request) {
        List<ExecutionDto> executions = getHabitStreak(request);
        return calculateSuccessRate(executions);
    }

    @Auditable(action = ActionType.GET)
    public List<ExecutionDto> getHabitStreak(ReportRequest request) {
        Habit habit = habitService.getHabitById(request.getHabitId());
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
        if (end.isBefore(endOfMonth) && !executed.isEmpty()) {
            result.add(new ExecutionDto(start, end, true));
            return result;
        }
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
