package com.fallt.service;

import com.fallt.dto.ExecutionDto;
import com.fallt.dto.HabitProgress;
import com.fallt.entity.ExecutionRate;
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
        progress.setSuccessRate(getSuccessHabitRate(habit, start, end));
        progress.setExecution(getHabitStreak(habit, start, end));
        return progress;
    }

    public int getSuccessHabitRate(Habit habit, LocalDate start, LocalDate end) {
        int totalCountHabit = calculatePerformedHabitDuringPeriod(habit.getExecutionRate(), start, end);
        int successHabitCount = (int) habit.getSuccessfulExecution().stream()
                .filter(d -> !d.isBefore(start) && !d.isAfter(end)).count();
        return Math.round((float) successHabitCount * 100 / totalCountHabit);
    }

    public List<ExecutionDto> getHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        return switch (habit.getExecutionRate()) {
            case DAILY -> getDailyHabitStreak(habit, start, end);
            case WEEKLY -> getWeeklyHabitStreak(habit, start, end);
            case MONTHLY -> getMonthlyHabitStreak(habit, start, end);
        };
    }

    public List<ExecutionDto> getDailyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
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

    public List<ExecutionDto> getWeeklyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<LocalDate> executed = new ArrayList<>(habit.getSuccessfulExecution());
        List<ExecutionDto> result = new ArrayList<>();
        int dateOfWeek = start.getDayOfWeek().getValue();
        LocalDate startOfWeek = start;
        LocalDate endOfWeek = start.plusDays((long) 7 - dateOfWeek);
        while (!endOfWeek.isAfter(end)) {
            if (executed.getFirst().isAfter(startOfWeek) && executed.getFirst().isBefore(endOfWeek)) {
                result.add(new ExecutionDto(startOfWeek, endOfWeek, true));
                executed.removeFirst();
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

    public List<ExecutionDto> getMonthlyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<LocalDate> executed = new ArrayList<>(habit.getSuccessfulExecution());
        List<ExecutionDto> result = new ArrayList<>();
        LocalDate startOfMonth = start;
        LocalDate endOfMonth = start.withDayOfMonth(start.getMonth().length(start.isLeapYear()));
        while (!endOfMonth.isAfter(end)) {
            if (executed.getFirst().isAfter(startOfMonth) && executed.getFirst().isBefore(endOfMonth)) {
                result.add(new ExecutionDto(startOfMonth, endOfMonth, true));
                executed.removeFirst();
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

    private int calculatePerformedHabitDuringPeriod(ExecutionRate rate, LocalDate start, LocalDate end) {
        int eventCount = 0;
        LocalDate currentDate = start;
        while (!currentDate.isAfter(end)) {
            switch (rate) {
                case DAILY:
                    eventCount++;
                    break;
                case WEEKLY:
                    if (currentDate.getDayOfWeek() == start.getDayOfWeek()) {
                        eventCount++;
                    }
                    break;
                case MONTHLY:
                    if (currentDate.getDayOfMonth() == start.getDayOfMonth()) {
                        eventCount++;
                    }
                    break;
            }
            currentDate = currentDate.plusDays(1);
        }
        return eventCount;
    }
}
