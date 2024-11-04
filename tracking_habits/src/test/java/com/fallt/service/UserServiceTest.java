package com.fallt.service;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.exception.EntityNotFoundException;
import com.fallt.repository.UserDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.fallt.TestConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

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
        User userFromDatabase = createUser();
        when(userDao.getUserByEmail(FIRST_USER_EMAIL)).thenReturn(Optional.of(userFromDatabase));

        User existedUser = userService.getUserByEmail(FIRST_USER_EMAIL);

        assertThat(existedUser.getEmail()).isEqualTo(FIRST_USER_EMAIL);
        assertThat(existedUser.getPassword()).isEqualTo(FIRST_USER_PASSWORD);
    }

    @Test
    @DisplayName("Попытка получения пользователя по отсутствующему email")
    void testGetUserByIncorrectEmail() {
        String email = "incorrectEmail";
        when(userDao.getUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    @DisplayName("Успешное добавление пользователя")
    void testCreateUser() {
        User userFromDb = createUser();
        UserResponse expected = new UserResponse(FIRST_USER_NAME, FIRST_USER_EMAIL);
        UpsertUserRequest upsertUserRequest = createRequest(FIRST_USER_EMAIL, FIRST_USER_PASSWORD);
        when(userDao.create(any(User.class))).thenReturn(userFromDb);

        UserResponse response = userService.saveUser(upsertUserRequest);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим email")
    void testCreateUserWithDuplicateEmail() {
        UpsertUserRequest request = createRequest(FIRST_USER_EMAIL, FIRST_USER_PASSWORD);
        when(userDao.getUserByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.saveUser(request));
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим паролем")
    void testCreateUserWithDuplicatePassword() {
        UpsertUserRequest request = createRequest(SECOND_USER_EMAIL, SECOND_USER_PASSWORD);
        when(userDao.getUserByPassword(request.getPassword())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.saveUser(request));
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void successUpdateUser() {
        User user = createUser();
        UpsertUserRequest request = createRequest(SECOND_USER_EMAIL, SECOND_USER_PASSWORD);
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(request.getPassword())).thenReturn(Optional.empty());
        when(userDao.update(any(User.class))).thenReturn(user);
        UserResponse expected = new UserResponse(FIRST_USER_NAME, SECOND_USER_EMAIL);

        UserResponse response = userService.updateUser(user.getEmail(), request);

        verify(userDao, times(1)).update(user);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего пароля")
    void testUpdateUserWithExistsPassword() {
        User user = createUser();
        UpsertUserRequest updateDto = UpsertUserRequest.builder().password(FIRST_USER_PASSWORD).name(SECOND_USER_NAME).email(SECOND_USER_EMAIL).build();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(updateDto.getPassword())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.updateUser(user.getEmail(), updateDto));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего email")
    void testUpdateUserWithExistsEmail() {
        User user = createUser();
        UpsertUserRequest updateDto = UpsertUserRequest.builder()
                .password(FIRST_USER_PASSWORD)
                .name(FIRST_USER_NAME)
                .email(FIRST_USER_EMAIL)
                .build();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(updateDto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.updateUser(user.getEmail(), updateDto));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        User existedUser = createUser();

        userService.deleteUser(existedUser.getEmail());

        verify(userDao, times(1)).delete(existedUser.getEmail());
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void blockUser() {
        User user = createUser();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));

        userService.blockingUser(user.getEmail());

        assertThat(user.isBlocked()).isTrue();
        verify(userDao, times(1)).update(user);
    }

    private UpsertUserRequest createRequest(String email, String password) {
        return UpsertUserRequest.builder()
                .name(FIRST_USER_NAME)
                .email(email)
                .password(password)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name(FIRST_USER_NAME)
                .password(FIRST_USER_PASSWORD)
                .email(FIRST_USER_EMAIL)
                .createAt(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .isBlocked(false)
                .build();
    }
}
