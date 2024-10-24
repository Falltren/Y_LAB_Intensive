package com.fallt.service;

import com.fallt.dto.request.UpsertUserRequest;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.repository.UserDao;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;

    private ConsoleOutput consoleOutput;

    private UserDao userDao;

    @BeforeEach
    void setup() {
        userDao = Mockito.mock(UserDao.class);
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        userService = new UserService(userDao, consoleOutput);
    }

//    @Test
//    @DisplayName("Получение всех пользователей")
//    void testGetAllUsers(){
//        List<User> habits = List.of(
//                User.builder().name("user1").password("pwd1").email("email1").build(),
//                User.builder().name("user2").password("pwd2").email("email2").build()
//        );
//        when(userDao.findAll()).thenReturn(habits);
//
//        List<User> expected = userService.getAllUsers();
//
//        assertThat(expected).hasSize(2);
//        assertThat(habits).isEqualTo(expected);
//    }

    @Test
    @DisplayName("Получение пользователя по email")
    void testGetUserByEmail() {
        String email = "user@user.user";
        User userFromDatabase = getUserFromDatabase();
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

//    @Test
//    @DisplayName("Успешное добавление пользователя")
//    void testCreateUser() {
//        UpsertUserRequest upsertUserRequest = createUserDto("user@user.user", "pwd");
//        when(userDao.create(any(User.class))).thenReturn(getUserFromDatabase());
//
//        User result = userService.createUser(upsertUserRequest);
//
//        assertEquals(upsertUserRequest.getName(), result.getName());
//        assertEquals(upsertUserRequest.getPassword(), result.getPassword());
//        assertEquals(upsertUserRequest.getEmail(), result.getEmail());
//    }
//
//    @Test
//    @DisplayName("Попытка сохранения пользователя с существующим email")
//    void testCreateUserWithDuplicateEmail() {
//        UpsertUserRequest user = createUserDto("user@user.user", "user1");
//        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(new User()));
//
//        User result = userService.createUser(user);
//
//        assertNull(result);
//        verify(consoleOutput).printMessage(Message.EMAIL_EXIST);
//    }
//
//    @Test
//    @DisplayName("Попытка сохранения пользователя с существующим паролем")
//    void testCreateUserWithDuplicatePassword() {
//        UpsertUserRequest user = createUserDto("user1@user.user", "user");
//        when(userDao.getUserByPassword(user.getPassword())).thenReturn(Optional.of(new User()));
//
//        User result = userService.createUser(user);
//
//        assertNull(result);
//        verify(consoleOutput).printMessage(Message.PASSWORD_EXIST);
//    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void successUpdateUser() {
        User user = getUserFromDatabase();
        UpsertUserRequest updateDto = UpsertUserRequest.builder().password("newPwd").name("newName").email("newEmail").build();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(updateDto.getPassword())).thenReturn(Optional.empty());


        userService.updateUser(user.getEmail(), updateDto);
        user.setPassword(updateDto.getPassword());
        user.setName(updateDto.getName());
        user.setEmail(updateDto.getEmail());

        verify(userDao, times(1)).update(user);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего пароля")
    void testUpdateUserWithExistsPassword() {
        User user = getUserFromDatabase();
        UpsertUserRequest updateDto = UpsertUserRequest.builder().password("newPwd").name("newName").email("newEmail").build();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(updateDto.getEmail())).thenReturn(Optional.empty());
        when(userDao.getUserByPassword(updateDto.getPassword())).thenReturn(Optional.of(new User()));

        userService.updateUser(user.getEmail(), updateDto);

        verify(consoleOutput).printMessage(Message.PASSWORD_EXIST);
        verify(userDao, times(0)).update(any(User.class));
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего email")
    void testUpdateUserWithExistsEmail() {
        User user = getUserFromDatabase();
        UpsertUserRequest updateDto = UpsertUserRequest.builder().password("newPwd").name("newName").email("newEmail").build();
        when(userDao.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userDao.getUserByEmail(updateDto.getEmail())).thenReturn(Optional.of(new User()));
        when(userDao.getUserByPassword(updateDto.getPassword())).thenReturn(Optional.empty());

        userService.updateUser(user.getEmail(), updateDto);

        verify(consoleOutput).printMessage(Message.EMAIL_EXIST);
        verify(userDao, times(0)).update(any(User.class));
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        User existedUser = getUserFromDatabase();

        userService.deleteUser(existedUser.getEmail());

        verify(userDao, times(1)).delete(existedUser.getEmail());
    }

    @Test
    @DisplayName("Блокировка пользователя")
    void blockUser(){
        User user = getUserFromDatabase();

        userService.blockingUser(user.getEmail());

        assertThat(user.isBlocked()).isTrue();
        verify(userDao, times(1)).update(user);
    }

    private UpsertUserRequest createUserDto(String email, String password) {
        return UpsertUserRequest.builder()
                .name("user")
                .email(email)
                .password(password)
                .build();
    }

    private User getUserFromDatabase() {
        return User.builder()
                .id(1L)
                .name("user")
                .password("pwd")
                .email("user@user.user")
                .build();
    }
}
