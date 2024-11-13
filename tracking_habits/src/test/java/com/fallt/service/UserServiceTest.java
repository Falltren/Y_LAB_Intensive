package com.fallt.service;

import com.fallt.domain.dto.request.UpsertUserRequest;
import com.fallt.domain.dto.response.UserResponse;
import com.fallt.domain.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.repository.UserDao;
import com.fallt.security.PasswordEncoder;
import com.fallt.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.fallt.TestConstant.FIRST_USER_EMAIL;
import static com.fallt.TestConstant.FIRST_USER_NAME;
import static com.fallt.TestConstant.FIRST_USER_PASSWORD;
import static com.fallt.TestConstant.SECOND_USER_EMAIL;
import static com.fallt.TestConstant.SECOND_USER_NAME;
import static com.fallt.TestConstant.SECOND_USER_PASSWORD;
import static com.fallt.TestConstant.USER_FROM_DATABASE;
import static com.fallt.TestConstant.USER_REQUEST;
import static com.fallt.TestConstant.USER_RESPONSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDao userDao;

    @Test
    @DisplayName("Получение всех пользователей")
    void testGetAllUsers() {
        List<User> users = List.of(
                User.builder().name(FIRST_USER_NAME).password(FIRST_USER_PASSWORD).email(FIRST_USER_EMAIL).build(),
                User.builder().name(SECOND_USER_NAME).password(SECOND_USER_PASSWORD).email(SECOND_USER_EMAIL).build()
        );
        List<UserResponse> expected = List.of(
                new UserResponse(FIRST_USER_NAME, FIRST_USER_EMAIL),
                new UserResponse(SECOND_USER_NAME, SECOND_USER_EMAIL)
        );

        when(userDao.findAll()).thenReturn(users);

        List<UserResponse> responseList = userService.getAllUsers();
        assertThat(responseList).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение пользователя по email")
    void testGetUserByEmail() {
        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.of(USER_FROM_DATABASE));

        User existedUser = userService.getUserByEmail(FIRST_USER_EMAIL);
        assertThat(existedUser.getEmail()).isEqualTo(FIRST_USER_EMAIL);
        assertThat(existedUser.getPassword()).isEqualTo(FIRST_USER_PASSWORD);
    }

    @Test
    @DisplayName("Попытка получения пользователя по отсутствующему email")
    void testGetUserByIncorrectEmail() {
        when(userDao.getUserByEmail(SECOND_USER_EMAIL)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail(SECOND_USER_EMAIL));
    }

    @Test
    @DisplayName("Успешное добавление пользователя")
    void testCreateUser() {
        when(userDao.create(any(User.class))).thenReturn(USER_FROM_DATABASE);

        UserResponse response = userService.saveUser(USER_REQUEST);
        assertThat(response).isEqualTo(USER_RESPONSE);
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим email")
    void testCreateUserWithDuplicateEmail() {
        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.of(USER_FROM_DATABASE));

        assertThrows(AlreadyExistException.class, () -> userService.saveUser(USER_REQUEST));
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим паролем")
    void testCreateUserWithDuplicatePassword() {
        String encodedPassword = "encodedPwd";
        when(passwordEncoder.encode(USER_REQUEST.getPassword())).thenReturn(encodedPassword);
        when(userDao.getUserByPassword(encodedPassword)).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.saveUser(USER_REQUEST));
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void successUpdateUser() {
        User user = User.builder()
                .email(SECOND_USER_EMAIL)
                .password(SECOND_USER_PASSWORD)
                .name(SECOND_USER_NAME)
                .build();

        when(passwordEncoder.encode(FIRST_USER_PASSWORD)).thenReturn(FIRST_USER_PASSWORD);
        when(userDao.getUserByEmail(SECOND_USER_EMAIL)).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(FIRST_USER_PASSWORD)).thenReturn(Optional.empty());
        when(userDao.update(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateUser(SECOND_USER_EMAIL, USER_REQUEST);

        verify(userDao, times(1)).update(user);
        assertThat(response).isEqualTo(USER_RESPONSE);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего пароля")
    void testUpdateUserWithExistsPassword() {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .email(SECOND_USER_EMAIL)
                .password(FIRST_USER_PASSWORD)
                .build();

        when(passwordEncoder.encode(FIRST_USER_PASSWORD)).thenReturn(FIRST_USER_PASSWORD);
        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.of(USER_FROM_DATABASE));
        when(userDao.getUserByPassword(FIRST_USER_PASSWORD)).thenReturn(Optional.of(USER_FROM_DATABASE));

        assertThrows(AlreadyExistException.class, () -> userService.updateUser(FIRST_USER_EMAIL, request));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего email")
    void testUpdateUserWithExistsEmail() {
        UpsertUserRequest request = UpsertUserRequest.builder()
                .email(FIRST_USER_EMAIL)
                .password(SECOND_USER_PASSWORD)
                .build();

        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.of(USER_FROM_DATABASE));

        assertThrows(AlreadyExistException.class, () -> userService.updateUser(FIRST_USER_EMAIL, request));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        userService.deleteUser(FIRST_USER_EMAIL);

        verify(userDao, times(1)).delete(FIRST_USER_EMAIL);
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void blockUser() {
        User user = User.builder()
                .email(FIRST_USER_EMAIL)
                .password(FIRST_USER_PASSWORD)
                .build();
        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.of(user));

        userService.blockingUser(user.getEmail());

        assertThat(user.isBlocked()).isTrue();
        verify(userDao, times(1)).update(user);
    }
}
