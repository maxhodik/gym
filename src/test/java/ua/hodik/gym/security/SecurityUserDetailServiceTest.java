package ua.hodik.gym.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.tets.util.TestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUserDetailServiceTest {
    public static final String USER_NAME = "Sam.Jonson";
    public static final String WRONG_USER_NAME = "WrongUserName";
    private final String userPath = "user.json";

    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final UserDetailsImpl expectedUserDetails = new UserDetailsImpl(expectedUser);


    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SecurityUserDetailService userDetailService;

    @Test
    void loadUserByUsername_ReturnUser() {
        //given
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.ofNullable(expectedUser));
        //when
        UserDetails userDetails = userDetailService.loadUserByUsername(USER_NAME);
        //then
        assertEquals(expectedUserDetails, userDetails);
        verify(userRepository).findByUserName(USER_NAME);
    }

    @Test
    void loadUserByUsername_WrongUserName_ThrowException() {
        //given
        when(userRepository.findByUserName(anyString())).thenThrow(new jakarta.persistence.EntityNotFoundException(String.format("User  %s not found", WRONG_USER_NAME)));
        //when
        jakarta.persistence.EntityNotFoundException exception = assertThrows(jakarta.persistence.EntityNotFoundException.class,
                () -> userDetailService.loadUserByUsername(WRONG_USER_NAME));
        //then
        assertEquals("User  WrongUserName not found", exception.getMessage());
    }
}