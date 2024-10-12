package com.fallt.service;

import com.fallt.dto.ExecutionDto;
import com.fallt.dto.HabitProgress;
import com.fallt.entity.Habit;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StatisticService {

    public HabitProgress getHabitProgress(Habit habit, LocalDate start, LocalDate end) {
        HabitProgress progress = new HabitProgress();
        progress.setTitle(habit.getTitle());
        List<ExecutionDto> executions = getHabitStreak(habit, start, end);
        progress.setSuccessRate(calculateSuccessRate(executions));
        progress.setExecution(executions);
        return progress;
    }

    public int getSuccessHabitRate(Habit habit, LocalDate start, LocalDate end) {
        List<ExecutionDto> executions = getHabitStreak(habit, start, end);
        return calculateSuccessRate(executions);
    }

    public List<ExecutionDto> getHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        return switch (habit.getExecutionRate()) {
            case DAILY -> getDailyHabitStreak(habit, start, end);
            case WEEKLY -> getWeeklyHabitStreak(habit, start, end);
            case MONTHLY -> getMonthlyHabitStreak(habit, start, end);
        };
    }

    public int calculateSuccessRate(List<ExecutionDto> executions) {
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
