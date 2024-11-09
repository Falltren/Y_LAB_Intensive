package com.fallt.service.impl;

import com.fallt.audit_starter.aop.Auditable;
import com.fallt.audit_starter.domain.entity.enums.ActionType;
import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.logging.annotation.Loggable;
import com.fallt.mapper.UserMapper;
import com.fallt.repository.UserDao;
import com.fallt.security.PasswordEncoder;
import com.fallt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Loggable
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Auditable(action = ActionType.GET)
    public List<UserResponse> getAllUsers() {
        return UserMapper.INSTANCE.toResponseList(userDao.findAll());
    }

    @Auditable(action = ActionType.CREATE)
    public UserResponse saveUser(UpsertUserRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        if (isExistsEmail(request.getEmail())) {
            throw new AlreadyExistException(MessageFormat.format("Электронная почта: {0} уже используется", request.getEmail()));
        }
        if (isExistsPassword(encodedPassword)) {
            throw new AlreadyExistException(MessageFormat.format("Пароль: {0} уже используется", request.getPassword()));
        }
        User user = UserMapper.INSTANCE.toEntity(request);
        user.setPassword(encodedPassword);
        return UserMapper.INSTANCE.toResponse(userDao.create(user));
    }

    @Auditable(action = ActionType.UPDATE)
    public void blockingUser(Long id) {
        User user = getUserById(id);
        user.setBlocked(true);
        user.setUpdateAt(LocalDateTime.now());
        userDao.update(user);
    }

    @Auditable(action = ActionType.UPDATE)
    public UserResponse updateUser(Long id, UpsertUserRequest updateUser) {
        User user = getUserById(id);
        if (updateUser.getEmail() != null && isExistsEmail(updateUser.getEmail())) {
            throw new AlreadyExistException(MessageFormat.format("Электронная почта: {0} уже используется", updateUser.getEmail()));
        }
        String encodedPassword = passwordEncoder.encode(updateUser.getPassword());
        if (updateUser.getPassword() != null && isExistsPassword(encodedPassword)) {
            throw new AlreadyExistException(MessageFormat.format("Пароль: {0} уже используется", updateUser.getPassword()));
        }
        UserMapper.INSTANCE.updateUserFromDto(updateUser, user);
        if (updateUser.getPassword() != null) {
            user.setPassword(encodedPassword);
        }
        user.setUpdateAt(LocalDateTime.now());
        return UserMapper.INSTANCE.toResponse(userDao.update(user));
    }

    @Auditable(action = ActionType.DELETE)
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    @Auditable(action = ActionType.GET)
    public User getUserById(Long id) {
        return userDao.getUserById(id).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format("Пользователь с ID: {0} не найден", id)));
    }

    @Auditable(action = ActionType.GET)
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(MessageFormat.format("Пользователь с email: {0} не найден", email)));
    }

    private boolean isExistsEmail(String email) {
        return userDao.getUserByEmail(email).isPresent();
    }

    private boolean isExistsPassword(String password) {
        return userDao.getUserByPassword(password).isPresent();
    }

}
