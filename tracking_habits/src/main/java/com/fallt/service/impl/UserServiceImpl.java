package com.fallt.service.impl;

import com.fallt.aop.audit.ActionType;
import com.fallt.aop.audit.Auditable;
import com.fallt.aop.logging.Loggable;
import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
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
        User savedUser = userDao.create(user);
        return UserMapper.INSTANCE.toResponse(savedUser);
    }

    @Auditable(action = ActionType.UPDATE)
    public void blockingUser(String email) {
        User user = getUserByEmail(email);
        user.setBlocked(true);
        user.setUpdateAt(LocalDateTime.now());
        userDao.update(user);
    }

    @Auditable(action = ActionType.UPDATE)
    public UserResponse updateUser(String email, UpsertUserRequest updateUser) {
        User user = getUserByEmail(email);
        if (updateUser.getEmail() != null && isExistsEmail(updateUser.getEmail())) {
            throw new AlreadyExistException(MessageFormat.format("Электронная почта: {0} уже используется", updateUser.getEmail()));
        }

        if (updateUser.getPassword() != null && isExistsPassword(updateUser.getPassword())) {
            throw new AlreadyExistException(MessageFormat.format("Пароль: {0} уже используется", updateUser.getPassword()));
        }
        UserMapper.INSTANCE.updateUserFromDto(updateUser, user);
        user.setUpdateAt(LocalDateTime.now());
        return UserMapper.INSTANCE.toResponse(userDao.update(user));
    }

    @Auditable(action = ActionType.DELETE)
    public void deleteUser(String email) {
        userDao.delete(email);
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
