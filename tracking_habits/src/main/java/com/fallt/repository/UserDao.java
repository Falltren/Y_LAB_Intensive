package com.fallt.repository;

import com.fallt.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User create(User user);

    void update(User user);

    void delete(User user);

    List<User> findAll();

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByPassword(String password);


}
