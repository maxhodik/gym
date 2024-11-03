package ua.hodik.gym.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;

@Service
public class SecurityUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username).orElseThrow(() -> new EntityNotFoundException(String.format("User %s not found", username)));
        return new UserDetailsImpl(user);
    }
}
