package com.fallt.repository.impl;

import com.fallt.domain.entity.User;
import com.fallt.domain.entity.enums.Role;
import com.fallt.exception.DBException;
import com.fallt.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.fallt.util.Constant.DELETE_USER_QUERY;
import static com.fallt.util.Constant.FIND_ALL_USERS_QUERY;
import static com.fallt.util.Constant.FIND_USER_BY_EMAIL_QUERY;
import static com.fallt.util.Constant.FIND_USER_BY_ID_QUERY;
import static com.fallt.util.Constant.FIND_USER_BY_PASSWORD_QUERY;
import static com.fallt.util.Constant.INSERT_USER_QUERY;
import static com.fallt.util.Constant.UPDATE_USER_QUERY;

@RequiredArgsConstructor
@Repository
public class UserDaoImpl implements UserDao {

    private final DataSource dataSource;

    @Override
    public User create(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setObject(5, user.getCreateAt());
            preparedStatement.setObject(6, user.getUpdateAt());
            preparedStatement.setBoolean(7, user.isBlocked());
            preparedStatement.setBoolean(8, user.isActive());
            preparedStatement.executeUpdate();
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long userId = generatedKeys.getLong(1);
                    user.setId(userId);
                }
            }
            return user;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public User update(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_QUERY)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setObject(5, user.getUpdateAt());
            preparedStatement.setBoolean(6, user.isBlocked());
            preparedStatement.setLong(7, user.getId());
            preparedStatement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER_QUERY)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(FIND_ALL_USERS_QUERY)) {
            while (resultSet.next()) {
                User user = instantiateUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_EMAIL_QUERY)) {
            preparedStatement.setString(1, email);
            User user = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    user = instantiateUser(resultSet);
                }
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserByPassword(String password) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_PASSWORD_QUERY)) {
            preparedStatement.setString(1, password);
            User user = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    user = instantiateUser(resultSet);
                }
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID_QUERY)) {
            preparedStatement.setLong(1, id);
            User user = null;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    user = instantiateUser(resultSet);
                }
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    private User instantiateUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .role(Role.valueOf(resultSet.getString("role")))
                .createAt(resultSet.getObject("create_at", LocalDateTime.class))
                .updateAt(resultSet.getObject("update_at", LocalDateTime.class))
                .isBlocked(resultSet.getBoolean("is_blocked"))
                .isActive(resultSet.getBoolean("is_active"))
                .build();
    }

}
