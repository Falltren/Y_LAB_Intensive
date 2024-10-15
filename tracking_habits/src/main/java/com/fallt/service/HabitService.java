package com.fallt.service;

import com.fallt.dto.HabitDto;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class HabitService {

    private final ConsoleOutput consoleOutput;


    public void createHabit(User user, HabitDto dto) {
        if (user.getHabits().stream().anyMatch(h -> h.getTitle().equals(dto.getTitle()))) {
            consoleOutput.printMessage(Message.HABIT_EXIST);
        } else {
            Habit habit = Habit.builder()
                    .title(dto.getTitle())
                    .text(dto.getText())
                    .executionRate(dto.getRate())
                    .user(user)
                    .createAt(LocalDate.now())
                    .build();
            user.getHabits().add(habit);
        }
    }

    public void updateHabit(User user, String title, HabitDto dto) {
        Optional<Habit> optionalHabit = findHabit(user, title);
        optionalHabit.ifPresent(habit -> updateNotNullableFields(habit, dto));
    }

    public void deleteHabit(User user, String title) {
        user.getHabits().removeIf(h -> h.getTitle().equals(title));
    }

    public List<Habit> getAllHabits(User user) {
        return user.getHabits();
    }

    public void confirmHabit(User user, String title, LocalDate date) {
        Optional<Habit> optionalHabit = findHabit(user, title);
        optionalHabit.ifPresent(habit -> habit.getSuccessfulExecution().add(date));
    }

    private Optional<Habit> findHabit(User user, String title) {
        return user.getHabits().stream()
                .filter(h -> h.getTitle().equals(title))
                .findFirst()
                .or(() -> {
                    consoleOutput.printMessage(Message.INCORRECT_HABIT_TITLE);
                    return Optional.empty();
                });
    }

    public Habit getHabitByTitle(User user, String title) {
        return findHabit(user, title).orElse(null);
    }

    private void updateNotNullableFields(Habit habit, HabitDto dto) {
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            habit.setTitle(dto.getTitle());
        }
        if (dto.getText() != null && !dto.getText().isBlank()) {
            habit.setText(dto.getText());
        }
        if (dto.getRate() != null && !dto.getRate().name().isBlank()) {
            habit.setExecutionRate(dto.getRate());
        }
    }
}
