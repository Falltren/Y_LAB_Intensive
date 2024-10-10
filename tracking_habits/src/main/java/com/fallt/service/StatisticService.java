package com.fallt.service;

import com.fallt.dto.ExecutionDailyDto;
import com.fallt.dto.ExecutionPeriodDto;
import com.fallt.entity.ExecutionRate;
import com.fallt.entity.Habit;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StatisticService {

    private final HabitService habitService;

    public int getSuccessHabitRate(Habit habit, LocalDate start, LocalDate end) {
        int totalCountHabit = calculatePerformedHabitDuringPeriod(habit.getExecutionRate(), start, end);
        int successHabitCount = (int) habit.getSuccessfulExecution().stream()
                .filter(d -> !d.isBefore(start) && !d.isAfter(end)).count();
        return Math.round((float) successHabitCount * 100 / totalCountHabit);
    }

    public List<ExecutionDailyDto> getDailyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<ExecutionDailyDto> result = new ArrayList<>();
        LocalDate currentDay = start;
        while (!currentDay.isAfter(end)) {
            if (habit.getSuccessfulExecution().contains(currentDay)) {
                result.add(new ExecutionDailyDto(currentDay, true));
            } else {
                result.add(new ExecutionDailyDto(currentDay, false));
            }
            currentDay = currentDay.plusDays(1);
        }
        return result;
    }

    public List<ExecutionPeriodDto> getWeeklyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<LocalDate> executed = new ArrayList<>(habit.getSuccessfulExecution());
        List<ExecutionPeriodDto> result = new ArrayList<>();
        int dateOfWeek = start.getDayOfWeek().getValue();
        LocalDate startOfWeek = start;
        LocalDate endOfWeek = start.plusDays((long) 7 - dateOfWeek);
        while (!endOfWeek.isAfter(end)) {
            if (executed.getFirst().isAfter(startOfWeek) && executed.getFirst().isBefore(endOfWeek)) {
                result.add(new ExecutionPeriodDto(startOfWeek, endOfWeek, true));
                executed.removeFirst();
            } else {
                result.add(new ExecutionPeriodDto(startOfWeek, endOfWeek, false));
            }
            startOfWeek = endOfWeek.plusDays(1);
            endOfWeek = endOfWeek.plusWeeks(1);
            if (endOfWeek.isAfter(end) && startOfWeek.isBefore(end) && !executed.isEmpty()) {
                result.add(new ExecutionPeriodDto(startOfWeek, end, true));
            } else if (endOfWeek.isAfter(end) && startOfWeek.isBefore(end) && executed.isEmpty()) {
                result.add(new ExecutionPeriodDto(startOfWeek, end, false));
            }
        }
        return result;
    }

    public List<ExecutionPeriodDto> getMonthlyHabitStreak(Habit habit, LocalDate start, LocalDate end) {
        List<LocalDate> executed = new ArrayList<>(habit.getSuccessfulExecution());
        List<ExecutionPeriodDto> result = new ArrayList<>();
        LocalDate startOfMonth = start;
        LocalDate endOfMonth = start.withDayOfMonth(start.getMonth().length(start.isLeapYear()));
        while (!endOfMonth.isAfter(end)) {
            if (executed.getFirst().isAfter(startOfMonth) && executed.getFirst().isBefore(endOfMonth)) {
                result.add(new ExecutionPeriodDto(startOfMonth, endOfMonth, true));
                executed.removeFirst();
            } else {
                result.add(new ExecutionPeriodDto(startOfMonth, endOfMonth, false));
            }
            startOfMonth = endOfMonth.plusDays(1);
            endOfMonth = endOfMonth.plusMonths(1);
            if (endOfMonth.isAfter(end) && startOfMonth.isBefore(end) && !executed.isEmpty()) {
                result.add(new ExecutionPeriodDto(startOfMonth, end, true));
            } else if (endOfMonth.isAfter(end) && startOfMonth.isBefore(end) && executed.isEmpty()) {
                result.add(new ExecutionPeriodDto(startOfMonth, end, false));
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
