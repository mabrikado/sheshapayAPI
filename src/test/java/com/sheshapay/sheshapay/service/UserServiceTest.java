package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.enums.ProfileType;
import com.sheshapay.sheshapay.enums.UserRole;
import com.sheshapay.sheshapay.exception.UserExists;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.ProfileRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_shouldCreateUserSuccessfully() throws Exception {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setUsername("lerato");
        form.setPassword("mypassword");
        form.setEmail("lerato@example.com");
        form.setRole(UserRole.CUSTOMER);
        form.setAddress("123 Main St");
        form.setFirstName("Lerato");
        form.setLastName("Buthelezi");
        form.setPhone("0812345678");
        form.setProfileType(ProfileType.CUSTOMER);
        form.setBusinessName("Lerato Cakes");

        when(userRepository.findByUsernameOrEmail("lerato", "lerato@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("mypassword")).thenReturn("ENCODED_PASS");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("lerato");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User created = userService.registerUser(form);

        // Assert
        assertNotNull(created);
        assertEquals("lerato", created.getUsername());
        verify(userRepository).save(any(User.class));
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void registerUser_shouldThrowWhenUserExists() {
        // Arrange
        RegisterForm form = new RegisterForm();
        form.setUsername("lerato");
        form.setEmail("lerato@example.com");
        form.setPassword("pass123");
        form.setRole(UserRole.CUSTOMER);

        User existing = new User();
        existing.setUsername("lerato");

        when(userRepository.findByUsernameOrEmail("lerato", "lerato@example.com"))
                .thenReturn(Optional.of(existing));

        // Act & Assert
        assertThrows(UserExists.class, () -> userService.registerUser(form));
        verify(userRepository, never()).save(any());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void getUsernames_shouldReturnOnlyCustomers() {
        User customer1 = new User();
        customer1.setUsername("lerato");
        customer1.setRole(UserRole.CUSTOMER);

        User admin = new User();
        admin.setUsername("admin");
        admin.setRole(UserRole.ADMIN);

        when(userRepository.findByUsernameContainsIgnoreCase("er"))
                .thenReturn(List.of(customer1, admin));

        List<String> result = userService.getUsernames("er");

        assertEquals(1, result.size());
        assertTrue(result.contains("lerato"));
        verify(userRepository).findByUsernameContainsIgnoreCase("er");
    }

    @Test
    void getBusinessNames_shouldReturnBusinessNames() {
        Profile p1 = new Profile();
        p1.setBusinessName("Lerato Cakes");

        Profile p2 = new Profile();
        p2.setBusinessName("Sipho Store");

        when(profileRepository.findByBusinessNameContainingIgnoreCase("a"))
                .thenReturn(List.of(p1, p2));

        List<String> result = userService.getBusinessNames("a");

        assertEquals(2, result.size());
        assertTrue(result.contains("Lerato Cakes"));
        assertTrue(result.contains("Sipho Store"));
    }
}
