package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.auth.Role;
import vitaliiev.resortASU.model.auth.User;
import vitaliiev.resortASU.repository.auth.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues() //todo add
            .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnorePaths("id", "password", "roles");
    private final UserRepository userRepository;

    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username));
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean create(User user) { //todo  fix return type
        try { // todo not needed?
            loadUserByUsername(user.getUsername());
            return false;
        } catch (UsernameNotFoundException e) {
            user.addRole(roleService.getUser());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            try {
                userRepository.save(user);
            } catch (DataIntegrityViolationException dve) {
                log.warn(dve.getMessage());
            }
            return true;
        }
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> find(User user) {
        Example<User> example = Example.of(user, SEARCH_CONDITIONS_MATCH_ALL);
        return userRepository.findAll(example, Sort.by("username"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User update(User user) {
        User userFromDb = userRepository.getReferenceById(user.getId());
        Role admin = roleService.getAdmin();
        if (userIsLastAdmin(userFromDb) && !user.getRoles().contains(admin)) {
            // do not allow delete admin role from last admin
            user.getRoles().add(admin);
            log.info("User {} is last user with admin role.", user.getUsername());
            log.info("Cant apply current set of roles to user {}. {} added to set of roles", user.getUsername(),
                    roleService.getAdmin().getName());
        }
        if (user.getRoles().isEmpty()) {
            user.getRoles().add(roleService.getUser());
            log.info("Cant apply empty set of roles to user {}. {} added to set of roles", user.getUsername(),
                    roleService.getUser().getName());
        }
        userFromDb.setEnabled(user.getEnabled());
        userFromDb.addRoles(user.getRoles());
        try {
            return userRepository.save(userFromDb);
        } catch (DataIntegrityViolationException dve) {
            log.warn(dve.getMessage());
            return null;
        }

    }

    public User changePassword(User user) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        User userFromDb = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userFromDb.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(userFromDb);
    }

    public void delete(Long id) {
        User user = userRepository.findUsersById(id);
        if (userIsLastAdmin(user)) {// do not allow to delete last user with role "admin"
            log.warn("Can't delete the only remaining user with admin rights");
        } else {
            userRepository.deleteById(id);
        }
    }

    private boolean userHasRole(User user, Role role) {
        return user.getRoles().contains(role);
    }

    private boolean userIsLastAdmin(User user) {
        Role admin = roleService.getAdmin();
        if (userHasRole(user, admin)) {
            return admin.getUsers().size() <= 1;
        }
        return false;
    }
}
