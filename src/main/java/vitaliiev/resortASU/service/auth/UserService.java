package vitaliiev.resortASU.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.entity.auth.User;
import vitaliiev.resortASU.repository.auth.RoleRepository;
import vitaliiev.resortASU.repository.auth.UserRepository;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean addUser(User user) {
        try {
            loadUserByUsername(user.getUsername());
            return false;
        } catch (UsernameNotFoundException e)  {
            user.setRoles(Collections.singleton(new Role("ROLE_USER")));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public Iterable<User> allUsers() {
        return userRepository.findAll();
    }
    // TODO: 19.10.2022 delete user 
}
