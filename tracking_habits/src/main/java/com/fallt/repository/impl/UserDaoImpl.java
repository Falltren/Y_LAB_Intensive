package com.fallt.repository.impl;

import com.fallt.entity.Role;
import com.fallt.entity.User;
import com.fallt.exception.DBException;
import com.fallt.repository.UserDao;
import com.fallt.util.DBUtils;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Класс предназначен для взаимодействия с таблицей users посредствам SQL запросов
 */
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final Connection connection;

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (name, password, email, role, create_at, update_at, is_blocked) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setObject(5, user.getCreateAt());
            preparedStatement.setObject(6, user.getUpdateAt());
            preparedStatement.setBoolean(7, user.isBlocked());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                long userId = generatedKeys.getLong(1);
                user.setId(userId);
            }
            connection.commit();
            DBUtils.closeResultSet(generatedKeys);
            return user;
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET name = ?, password = ?, email = ?, role = ?, update_at = ?, is_blocked = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getRole().name());
            preparedStatement.setObject(5, user.getUpdateAt());
            preparedStatement.setBoolean(6, user.isBlocked());
            preparedStatement.setLong(7, user.getId());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                User user = instantiateUser(resultSet);
                users.add(user);
            }
            DBUtils.closeResultSet(resultSet);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return users;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            while (resultSet.next()) {
                user = instantiateUser(resultSet);
            }
            DBUtils.closeResultSet(resultSet);
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public Optional<User> getUserByPassword(String password) {
        String sql = "SELECT * FROM users WHERE password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            while (resultSet.next()) {
                user = instantiateUser(resultSet);
            }
            DBUtils.closeResultSet(resultSet);
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM users";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
            connection.commit();
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
                .build();
    }
}
