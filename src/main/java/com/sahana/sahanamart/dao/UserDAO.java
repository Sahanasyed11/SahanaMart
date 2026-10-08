package com.sahana.sahanamart.dao;

import com.sahana.sahanamart.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User create(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findAll();
    boolean update(User user);
    boolean updatePassword(Long userId, String newPasswordHash);
    boolean delete(Long id);
    long count();
}
