package com.vfc.vfc_backend;


import com.vfc.vfc_backend.model.User;
import com.vfc.vfc_backend.repository.UserRepository;
import com.vfc.vfc_backend.service.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    public UserRepository userRepository;

    @InjectMocks
    public UserServiceImpl userServiceImpl;

    private static final String TEST_NAME = "test";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User(TEST_NAME, TEST_EMAIL, TEST_PASSWORD);
        testUser.setUserId(0);
    }

    @Test
    public void findUserIfEmailExists(){
        when(userRepository.findByUserEmail(testUser.getUserEmail())).thenReturn(Optional.ofNullable(testUser));
        User result = userServiceImpl.findByUseremail(TEST_EMAIL);

        assert result.equals(testUser);
    }

    @Test
    public void returnNullUserIfEmailDoesNotExist(){
        when(userRepository.findByUserEmail(testUser.getUserEmail())).thenReturn(Optional.empty());
        User result = userServiceImpl.findByUseremail(TEST_EMAIL);

        assert result == null;
    }

    @Test
    public void findUserIfIdExists(){
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.ofNullable(testUser));
        User result = userServiceImpl.findById(0);

        assert result.equals(testUser);
    }

    @Test
    public void returnNullUserIfIdDoesNotExist(){
        when(userRepository.findById(testUser.getUserId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,() -> userServiceImpl.findById(0));
    }

}