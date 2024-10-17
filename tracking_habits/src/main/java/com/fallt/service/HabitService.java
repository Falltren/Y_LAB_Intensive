package com.fallt.service;

import com.fallt.dto.HabitDto;
import com.fallt.entity.Habit;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.HabitDao;
import com.fallt.util.Message;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class HabitService {

    private final ConsoleOutput consoleOutput;

    private final HabitDao habitDao;


    public void createHabit(User user, HabitDto dto) {
        if (findHabit(user, dto.getTitle()).isPresent()) {
            consoleOutput.printMessage(Message.HABIT_EXIST);
        } else {
            Habit habit = Habit.builder()
                    .title(dto.getTitle())
                    .text(dto.getText())
                    .executionRate(dto.getRate())
                    .user(user)
                    .createAt(LocalDate.now())
                    .build();
            habitDao.save(habit);
        }
    }

    public void updateHabit(User user, String title, HabitDto dto) {
        Optional<Habit> optionalHabit = findHabit(user, title);
        if (optionalHabit.isEmpty()) {
            consoleOutput.printMessage(Message.INCORRECT_HABIT_TITLE);
            return;
        }
        Habit habit = optionalHabit.get();
        habitDao.update(updateNotNullableFields(habit, dto));
    }

    public void deleteHabit(User user, String title) {
        habitDao.delete(user.getId(), title);
    }

    public List<Habit> getAllHabits(User user) {
        return habitDao.getAllUserHabits(user.getId());
    }

    public void confirmHabit(User user, String title, LocalDate date) {
        Optional<Habit> optionalHabit = findHabit(user, title);
        optionalHabit.ifPresent(habit -> habit.getSuccessfulExecution().add(date));
    }

    private Optional<Habit> findHabit(User user, String title) {
        return habitDao.findHabitByTitleAndUserId(user.getId(), title);
    }

    public Habit getHabitByTitle(User user, String title) {
        return findHabit(user, title).orElse(null);
    }

    private Habit updateNotNullableFields(Habit habit, HabitDto dto) {
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            habit.setTitle(dto.getTitle());
        }
        if (dto.getText() != null && !dto.getText().isBlank()) {
            habit.setText(dto.getText());
        }
        if (dto.getRate() != null && !dto.getRate().name().isBlank()) {
            habit.setExecutionRate(dto.getRate());
        }
        return habit;
    }
}
