package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
            .withIgnoreNullValues() //todo add roles
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

    @Caching(
            put = {@CachePut(cacheNames = "users", key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = "usersList", allEntries = true)}
    )
    public User create(User user) throws DataIntegrityViolationException {
        user.addRole(roleService.getUser());
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Cacheable(cacheNames = "users", key = "#id")
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

    @Cacheable(cacheNames = "usersList")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Caching(
            put = {@CachePut(cacheNames = "users", key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = "usersList", allEntries = true)}
    )
    public User update(User user) {
        // the parameter we get here doesnt contain password/ we need to get model from DB
        User userFromDb = this.findUserById(user.getId()); // uses cache
        user.setPassword(userFromDb.getPassword());
        Role admin = roleService.getAdmin();
        if (userIsLastAdmin(userFromDb) && !user.getRoles().contains(admin)) {
            // do not allow delete admin role from last admin
            user.getRoles().add(admin);
            log.info("User {} is last user with admin role.", user.getUsername());
            log.info("Cant apply current set of roles to user {}. {} added to set of roles", user.getUsername(),
                   admin.getName());
        }
        if (user.getRoles().isEmpty()) {
            user.getRoles().add(roleService.getUser());
            log.info("Cant apply empty set of roles to user {}. {} added to set of roles", user.getUsername(),
                    roleService.getUser().getName());
        }
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException dve) {
            log.warn(dve.getMessage());
            return null;
        }

    }
    @Caching(
            put = {@CachePut(cacheNames = "users", key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = "usersList", allEntries = true)}
    )
    public User changePassword(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Caching(
            evict = {@CacheEvict(cacheNames = "users", key = "#id"),
                    @CacheEvict(cacheNames = "usersList", allEntries = true)}
    )
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
