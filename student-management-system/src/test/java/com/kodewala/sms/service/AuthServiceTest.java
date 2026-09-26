package com.kodewala.sms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kodewala.sms.dto.ChangePasswordRequest;
import com.kodewala.sms.entity.User;
import com.kodewala.sms.exception.AccountPendingException;
import com.kodewala.sms.exception.AccountRejectedException;
import com.kodewala.sms.repository.UserRepository;
import com.kodewala.sms.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    private User approvedUser;

    @BeforeEach
    void setUp() {
        approvedUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded-old-password")
                .role("USER")
                .status("APPROVED")
                .build();
    }

    // =========================================================
    // LOGIN TESTS
    // =========================================================

    @Test
    void authenticateAndGenerateToken_shouldReturnTokenForApprovedUser() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(approvedUser));

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        String token = authService.authenticateAndGenerateToken(
                "testuser",
                "password"
        );

        assertNotNull(token);
        assertEquals("jwt-token", token);

        verify(authenticationManager).authenticate(any(
                UsernamePasswordAuthenticationToken.class));

        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void authenticateAndGenerateToken_shouldTrimUsername() {

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(approvedUser));

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        String token = authService.authenticateAndGenerateToken(
                "  testuser  ",
                "password"
        );

        assertEquals("jwt-token", token);

        verify(userRepository)
                .findByUsername("testuser");

        verify(authenticationManager).authenticate(
                argThat(argument -> {
                    UsernamePasswordAuthenticationToken authToken =
                            (UsernamePasswordAuthenticationToken) argument;

                    return "testuser".equals(authToken.getPrincipal())
                            && "password".equals(authToken.getCredentials());
                })
        );
    }

    @Test
    void authenticateAndGenerateToken_shouldRejectPendingUser() {

        User pendingUser = User.builder()
                .id(2L)
                .username("pendinguser")
                .password("encoded-password")
                .role("USER")
                .status("PENDING")
                .build();

        when(userRepository.findByUsername("pendinguser"))
                .thenReturn(Optional.of(pendingUser));

        AccountPendingException exception =
                assertThrows(
                        AccountPendingException.class,
                        () -> authService.authenticateAndGenerateToken(
                                "pendinguser",
                                "password"
                        )
                );

        assertEquals(
                "Your account is pending administrator approval.",
                exception.getMessage()
        );

        verify(authenticationManager, never())
                .authenticate(any());

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void authenticateAndGenerateToken_shouldRejectRejectedUser() {

        User rejectedUser = User.builder()
                .id(3L)
                .username("rejecteduser")
                .password("encoded-password")
                .role("USER")
                .status("REJECTED")
                .build();

        when(userRepository.findByUsername("rejecteduser"))
                .thenReturn(Optional.of(rejectedUser));

        AccountRejectedException exception =
                assertThrows(
                        AccountRejectedException.class,
                        () -> authService.authenticateAndGenerateToken(
                                "rejecteduser",
                                "password"
                        )
                );

        assertEquals(
                "Your account registration was rejected.",
                exception.getMessage()
        );

        verify(authenticationManager, never())
                .authenticate(any());

        verify(jwtService, never())
                .generateToken(any());
    }

    @Test
    void authenticateAndGenerateToken_shouldAllowAuthenticationWhenUserNotFoundInRepository() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-token");

        String token = authService.authenticateAndGenerateToken(
                "unknown",
                "password"
        );

        assertEquals("jwt-token", token);

        verify(authenticationManager).authenticate(any(
                UsernamePasswordAuthenticationToken.class));

        verify(jwtService).generateToken(userDetails);
    }

    // =========================================================
    // CHANGE PASSWORD TESTS
    // =========================================================

    @Test
    void changePassword_shouldChangePasswordSuccessfully() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(approvedUser));

        when(passwordEncoder.matches(
                "OldPassword123",
                "encoded-old-password"
        )).thenReturn(true);

        when(passwordEncoder.matches(
                "NewPassword123",
                "encoded-old-password"
        )).thenReturn(false);

        when(passwordEncoder.encode("NewPassword123"))
                .thenReturn("encoded-new-password");

        authService.changePassword(
                "testuser",
                request
        );

        assertEquals(
                "encoded-new-password",
                approvedUser.getPassword()
        );

        verify(passwordEncoder)
                .encode("NewPassword123");

        verify(userRepository)
                .save(approvedUser);
    }

    @Test
    void changePassword_shouldRejectUnknownUser() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.changePassword(
                                "unknown",
                                request
                        )
                );

        assertEquals(
                "User account not found",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void changePassword_shouldRejectIncorrectCurrentPassword() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("WrongPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("NewPassword123");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(approvedUser));

        when(passwordEncoder.matches(
                "WrongPassword123",
                "encoded-old-password"
        )).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.changePassword(
                                "testuser",
                                request
                        )
                );

        assertEquals(
                "Current password is incorrect",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void changePassword_shouldRejectMismatchedPasswords() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("NewPassword123");
        request.setConfirmPassword("DifferentPassword123");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(approvedUser));

        when(passwordEncoder.matches(
                "OldPassword123",
                "encoded-old-password"
        )).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.changePassword(
                                "testuser",
                                request
                        )
                );

        assertEquals(
                "New password and confirm password do not match",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any());
    }

    @Test
    void changePassword_shouldRejectSamePassword() {

        ChangePasswordRequest request =
                new ChangePasswordRequest();

        request.setCurrentPassword("OldPassword123");
        request.setNewPassword("OldPassword123");
        request.setConfirmPassword("OldPassword123");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(approvedUser));

        when(passwordEncoder.matches(
                "OldPassword123",
                "encoded-old-password"
        )).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.changePassword(
                                "testuser",
                                request
                        )
                );

        assertEquals(
                "New password must be different from current password",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(any());

        verify(userRepository, never())
                .save(any());
    }
}