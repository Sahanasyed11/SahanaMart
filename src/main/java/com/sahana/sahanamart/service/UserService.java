package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.UserDAO;
import com.sahana.sahanamart.dao.UserDAOImpl;
import com.sahana.sahanamart.dto.LoginRequestDTO;
import com.sahana.sahanamart.dto.RegisterRequestDTO;
import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.exception.AppException;
import com.sahana.sahanamart.exception.ResourceNotFoundException;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.PasswordUtil;
import com.sahana.sahanamart.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAOImpl();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserResponseDTO register(RegisterRequestDTO dto) {
        // Validation check before DAO
        ValidationUtil.validateRegistration(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getRole());

        // Admin cannot be registered publicly
        if ("ADMIN".equalsIgnoreCase(dto.getRole())) {
            throw new ValidationException("Admin accounts cannot be registered publicly",
                    Collections.singletonMap("role", "Admin is a seeded account only"));
        }

        if (userDAO.existsByEmail(dto.getEmail())) {
            throw new ValidationException("Email is already registered",
                    Collections.singletonMap("email", "An account with this email already exists"));
        }

        String hashedPassword = PasswordUtil.hashPassword(dto.getPassword());
        User user = new User();
        user.setName(dto.getName().trim());
        user.setEmail(dto.getEmail().trim().toLowerCase());
        user.setPasswordHash(hashedPassword);
        user.setRole(dto.getRole().toUpperCase());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        User created = userDAO.create(user);
        logger.info("Successfully registered user ID: {}, Role: {}", created.getId(), created.getRole());
        return UserResponseDTO.fromEntity(created);
    }

    public User login(LoginRequestDTO dto) {
        ValidationUtil.validateLogin(dto.getEmail(), dto.getPassword());

        User user = userDAO.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!PasswordUtil.checkPassword(dto.getPassword(), user.getPasswordHash())) {
            logger.warn("Failed login attempt for email: {}", dto.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        logger.info("Successful login for user ID: {}, Role: {}", user.getId(), user.getRole());
        return user;
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userDAO.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserResponseDTO.fromEntity(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userDAO.findAll().stream()
                .map(UserResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean deleteUser(Long id) {
        return userDAO.delete(id);
    }

    public long getTotalUserCount() {
        return userDAO.count();
    }
}