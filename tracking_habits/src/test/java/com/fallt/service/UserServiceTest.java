package com.fallt.service;

import com.fallt.dto.UserDto;
import com.fallt.entity.User;
import com.fallt.out.ConsoleOutput;
import com.fallt.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    private UserService userService;

    private ConsoleOutput consoleOutput;

    @BeforeEach
    void setup() {
        consoleOutput = Mockito.mock(ConsoleOutput.class);
        userService = new UserService(consoleOutput);
    }

    @Test
    @DisplayName("Получение пользователя по email")
    void testGetUserByEmail() {
        UserDto userDto = createUserDto("user@user.user", "user");
        userService.createUser(userDto);

        User existedUser = userService.getUserByEmail(userDto.getEmail());

        assertThat(existedUser.getEmail()).isEqualTo("user@user.user");
        assertThat(existedUser.getPassword()).isEqualTo("user");
    }

    @Test
    @DisplayName("Попытка получения пользователя по отсутствующему email")
    void testGetUserByIncorrectEmail() {
        UserDto userDto = createUserDto("user@user.user", "user");
        userService.createUser(userDto);

        User existedUser = userService.getUserByEmail("some@email.email");

        assertThat(existedUser).isNull();
        verify(consoleOutput).printMessage(Message.INCORRECT_EMAIL);
    }

    @Test
    @DisplayName("Успешное добавление пользователя")
    void testCreateUser() {
        UserDto userDto = createUserDto("user@user.user", "user");

        userService.createUser(userDto);

        assertThat(userService.getAllUsers).hasSize(1);
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим email")
    void testCreateUserWithDuplicateEmail() {
        UserDto user1 = createUserDto("user@user.user", "user1");
        UserDto user2 = createUserDto("user@user.user", "user2");
        userService.createUser(user1);

        userService.createUser(user2);

        verify(consoleOutput).printMessage(Message.EMAIL_EXIST);
        assertThat(userService.getAllUsers).hasSize(1);
    }

    @Test
    @DisplayName("Попытка сохранения пользователя с существующим паролем")
    void testCreateUserWithDuplicatePassword() {
        UserDto user1 = createUserDto("user1@user.user", "user");
        UserDto user2 = createUserDto("user2@user.user", "user");
        userService.createUser(user1);

        userService.createUser(user2);

        verify(consoleOutput).printMessage(Message.PASSWORD_EXIST);
        assertThat(userService.getAllUsers).hasSize(1);
    }

    @Test
    @DisplayName("Обновление данных о пользователе")
    void successUpdateUser() {
        UserDto userDto = createUserDto("user@user.user", "user");
        UserDto updateDto = UserDto.builder().password("newPwd").name("newName").email("newEmail").build();
        userService.createUser(userDto);

        userService.updateUser(userDto.getEmail(), updateDto);
        User updatedUser = userService.getUserByEmail("newEmail");

        assertThat(updatedUser.getPassword()).isEqualTo("newPwd");
        assertThat(updatedUser.getName()).isEqualTo("newName");
        assertThat(updatedUser.getEmail()).isEqualTo("newEmail");
        assertThat(userService.getAllUsers).hasSize(1);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего пароля")
    void testUpdateUserWithExistsPassword() {
        UserDto userDto1 = createUserDto("user1@user.user", "user1");
        UserDto userDto2 = createUserDto("user2@user.user", "user2");
        UserDto updateDto = UserDto.builder().password("user2").build();
        userService.createUser(userDto1);
        userService.createUser(userDto2);

        userService.updateUser("user1@user.user", updateDto);
        User user = userService.getUserByEmail("user1@user.user");

        assertThat(user.getPassword()).isEqualTo("user1");
        verify(consoleOutput).printMessage(Message.PASSWORD_EXIST);
    }

    @Test
    @DisplayName("Попытка обновления данных о пользователе с использованием существующего email")
    void testUpdateUserWithExistsEmail() {
        UserDto userDto1 = createUserDto("user1@user.user", "user1");
        UserDto userDto2 = createUserDto("user2@user.user", "user2");
        UserDto updateDto = UserDto.builder().email("user2@user.user").build();
        userService.createUser(userDto1);
        userService.createUser(userDto2);

        userService.updateUser("user1@user.user", updateDto);
        User user = userService.getUserByEmail("user1@user.user");

        assertThat(user.getPassword()).isEqualTo("user1");
        verify(consoleOutput).printMessage(Message.EMAIL_EXIST);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser() {
        UserDto userDto = createUserDto("user@user.user", "user");
        userService.createUser(userDto);
        assertThat(userService.getAllUsers).hasSize(1);

        userService.deleteUser(userDto.getEmail());

        assertThat(userService.getAllUsers).isEmpty();
    }

    private UserDto createUserDto(String email, String password) {
        return UserDto.builder()
                .name("user")
                .email(email)
                .password(password)
                .build();
    }
}
