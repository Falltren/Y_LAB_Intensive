package com.fallt.service.impl;

import com.fallt.aop.audit.ActionType;
import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.domain.dto.request.HabitConfirmRequest;
import com.fallt.domain.dto.request.UpsertHabitRequest;
import com.fallt.domain.dto.response.HabitExecutionResponse;
import com.fallt.domain.dto.response.HabitResponse;
import com.fallt.domain.entity.Habit;
import com.fallt.domain.entity.HabitExecution;
import com.fallt.domain.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.mapper.HabitMapper;
import com.fallt.repository.HabitDao;
import com.fallt.repository.HabitExecutionDao;
import com.fallt.service.HabitService;
import com.fallt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@Loggable
@Service
public class HabitServiceImpl implements HabitService {

    private final HabitDao habitDao;
    private final HabitExecutionDao executionDao;
    private final UserService userService;

    @Auditable(action = ActionType.CREATE)
    public HabitResponse saveHabit(String userEmail, UpsertHabitRequest request) {
        User user = userService.getUserByEmail(userEmail);
        if (isExistedHabit(user.getId(), request.getTitle())) {
            throw new AlreadyExistException("Привычка с указанным названием уже существует");
        }
        Habit habit = HabitMapper.INSTANCE.toEntity(request);
        habit.setUser(user);
        return HabitMapper.INSTANCE.toResponse(habitDao.save(habit));
    }

    @Auditable(action = ActionType.UPDATE)
    public HabitResponse updateHabit(String userEmail, String title, UpsertHabitRequest request) {
        User user = userService.getUserByEmail(userEmail);
        if (!isExistedHabit(user.getId(), title)) {
            throw new EntityNotFoundException(MessageFormat.format("У вас отсутствует привычка с указанным названием: {0}", title));
        }
        if (request.getTitle() != null && isExistedHabit(user.getId(), request.getTitle())) {
            throw new AlreadyExistException("Привычка с указанным названием уже существует");
        }
        Habit habit = getHabitByTitle(user, title);
        HabitMapper.INSTANCE.updateHabitFromDto(request, habit);
        return HabitMapper.INSTANCE.toResponse(habitDao.update(habit));
    }

    @Auditable(action = ActionType.DELETE)
    public void deleteHabit(String email, String title) {
        User user = userService.getUserByEmail(email);
        habitDao.delete(user.getId(), title);
    }

    @Auditable(action = ActionType.GET)
    public List<HabitResponse> getAllHabits(String email) {
        User user = userService.getUserByEmail(email);
        return HabitMapper.INSTANCE.toResponseList(habitDao.getAllUserHabits(user.getId()));
    }

    @Auditable(action = ActionType.CREATE)
    public HabitExecutionResponse confirmHabit(String email, HabitConfirmRequest request) {
        User user = userService.getUserByEmail(email);
        if (!isExistedHabit(user.getId(), request.getTitle())) {
            throw new EntityNotFoundException(MessageFormat.format("У вас отсутствует привычка с указанным названием: {0}", request.getTitle()));
        }
        Habit habit = getHabitByTitle(user, request.getTitle());
        HabitExecution habitExecution = HabitExecution.builder()
                .habit(habit)
                .date(request.getDate())
                .build();
        HabitExecutionResponse response = HabitMapper.INSTANCE.toExecutionResponse(executionDao.save(habitExecution));
        response.setTitle(request.getTitle());
        return response;
    }

    @Auditable(action = ActionType.GET)
    public Habit getHabitByTitle(User user, String title) {
        return habitDao.findHabitByTitleAndUserId(user.getId(), title).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format("У вас отсутствует привычка с указанным названием: {0}", title))
        );
    }

    private boolean isExistedHabit(Long userId, String title) {
        return habitDao.findHabitByTitleAndUserId(userId, title).isPresent();
    }
}
