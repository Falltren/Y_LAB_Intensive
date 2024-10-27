package com.fallt.service;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.dto.response.UserResponse;
import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.AlreadyExistException;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.UserDao;
import com.fallt.util.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private ConsoleOutput consoleOutput;

    @Mock
    private UserDao userDao;

    @Test
    @DisplayName("Получение всех пользователей")
    void testGetAllUsers() {
        List<User> users = List.of(
                User.builder().name("user1").password("pwd1").email("email1").build(),
                User.builder().name("user2").password("pwd2").email("email2").build()
        );
        List<UserResponse> expected = List.of(
                new UserResponse("user1", "email1"),
                new UserResponse("user2", "email2")
        );
        when(userDao.findAll()).thenReturn(users);

        List<UserResponse> responseList = userService.getAllUsers();

        assertThat(responseList).isEqualTo(expected);
    }

    @Test
    @DisplayName("Получение пользователя по email")
    void testGetUserByEmail() {
        String email = "user@user.user";
        User userFromDatabase = createUser();
        when(userDao.getUserByEmail("user@user.user")).thenReturn(Optional.of(userFromDatabase));

        User existedUser = userService.getUserByEmail(email);

        assertThat(existedUser.getEmail()).isEqualTo("user@user.user");
        assertThat(existedUser.getPassword()).isEqualTo("pwd");
    }

    @Test
    @DisplayName("Попытка получения пользователя по отсутствующему email")
    void testGetUserByIncorrectEmail() {
        String email = "incorrectEmail";
        when(userDao.getUserByEmail(email)).thenReturn(Optional.empty());

        User existedUser = userService.getUserByEmail(email);

        assertThat(existedUser).isNull();
        verify(consoleOutput).printMessage(Message.INCORRECT_EMAIL);
    }

    @Test
    @DisplayName("Успешное добавление пользователя")
    void testCreateUser() {
        User userFromDb = createUser();
        UserResponse expected = new UserResponse("user", "user@user.user");
        UpsertUserRequest upsertUserRequest = createRequest("user@user.user", "pwd");
        when(userDao.create(any(User.class))).thenReturn(userFromDb);

        UserResponse response = userService.saveUser(upsertUserRequest);

        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим email")
    void testCreateUserWithDuplicateEmail() {
        UpsertUserRequest request = createRequest("user@user.user", "user1");
        when(userDao.getUserByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.saveUser(request));
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим паролем")
    void testCreateUserWithDuplicatePassword() {
        UpsertUserRequest request = createRequest("user1@user.user", "user");
        when(userDao.getUserByPassword(request.getPassword())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.saveUser(request));
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void successUpdateUser() {
        User user = createUser();
        UpsertUserRequest request = createRequest("email", "pwd1");
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(request.getPassword())).thenReturn(Optional.empty());
        when(userDao.update(any(User.class))).thenReturn(user);
        UserResponse expected = new UserResponse("user", "email");

        UserResponse response = userService.updateUser(user.getEmail(), request);

        verify(userDao, times(1)).update(user);
        assertThat(response).isEqualTo(expected);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего пароля")
    void testUpdateUserWithExistsPassword() {
        User user = createUser();
        UpsertUserRequest updateDto = UpsertUserRequest.builder().password("newPwd").name("newName").email("newEmail").build();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(updateDto.getPassword())).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistException.class, () -> userService.updateUser(user.getEmail(), updateDto));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего email")
    void testUpdateUserWithExistsEmail() {
        User user = createUser();
        UpsertUserRequest updateDto = UpsertUserRequest.builder().password("newPwd").name("newName").email("newEmail").build();
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
                .name("user")
                .email(email)
                .password(password)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("user")
                .password("pwd")
                .email("user@user.user")
                .createAt(LocalDateTime.now())
                .role(Role.ROLE_USER)
                .isBlocked(false)
                .build();
    }
}
