package com.sahana.sahanamart.service;

import com.sahana.sahanamart.dao.UserDAO;
import com.sahana.sahanamart.dto.LoginRequestDTO;
import com.sahana.sahanamart.dto.RegisterRequestDTO;
import com.sahana.sahanamart.dto.UserResponseDTO;
import com.sahana.sahanamart.exception.UnauthorizedException;
import com.sahana.sahanamart.exception.ValidationException;
import com.sahana.sahanamart.model.User;
import com.sahana.sahanamart.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    public void setup() {
        userService = new UserService(userDAO);
    }

    @Test
    public void testRegisterSuccess() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Jane Buyer", "jane@example.com", "Password@123", "BUYER", "9876543210", "123 Street"
        );

        when(userDAO.existsByEmail("jane@example.com")).thenReturn(false);
        when(userDAO.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        UserResponseDTO result = userService.register(dto);
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Jane Buyer", result.getName());
        assertEquals("jane@example.com", result.getEmail());
        assertEquals("BUYER", result.getRole());
    }

    @Test
    public void testAdminRegistrationBlocked() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Hacker Admin", "admin@fake.com", "Password@123", "ADMIN", "9876543210", "Nowhere"
        );

        assertThrows(ValidationException.class, () -> userService.register(dto));
        verify(userDAO, never()).create(any(User.class));
    }

    @Test
    public void testDuplicateEmailBlocked() {
        RegisterRequestDTO dto = new RegisterRequestDTO(
                "Duplicate User", "existing@example.com", "Password@123", "BUYER", "9876543210", "Street"
        );

        when(userDAO.existsByEmail("existing@example.com")).thenReturn(true);
        assertThrows(ValidationException.class, () -> userService.register(dto));
    }

    @Test
    public void testLoginSuccess() {
        String rawPass = "Secret@123";
        String hashedPass = PasswordUtil.hashPassword(rawPass);

        User user = new User();
        user.setId(5L);
        user.setName("Real User");
        user.setEmail("real@example.com");
        user.setPasswordHash(hashedPass);
        user.setRole("BUYER");

        when(userDAO.findByEmail("real@example.com")).thenReturn(Optional.of(user));

        User loggedIn = userService.login(new LoginRequestDTO("real@example.com", rawPass));
        assertNotNull(loggedIn);
        assertEquals(5L, loggedIn.getId());
    }

    @Test
    public void testLoginInvalidPasswordThrows() {
        User user = new User();
        user.setId(5L);
        user.setEmail("real@example.com");
        user.setPasswordHash(PasswordUtil.hashPassword("CorrectPass"));

        when(userDAO.findByEmail("real@example.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> {
            userService.login(new LoginRequestDTO("real@example.com", "WrongPassword"));
        });
    }
}
