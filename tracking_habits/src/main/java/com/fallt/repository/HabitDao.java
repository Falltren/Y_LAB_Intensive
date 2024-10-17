package com.fallt.repository;

import com.fallt.entity.Habit;
import com.fallt.util.Fetch;

import java.util.List;
import java.util.Optional;

public interface HabitDao {

    void save(Habit habit);

    void update(Habit habit);

    List<Habit> getAllUserHabits(Long userId, Fetch fetchType);

    Optional<Habit> findHabitByTitleAndUserId(Long userId, String title);

    void delete(Long id, String title);

}
