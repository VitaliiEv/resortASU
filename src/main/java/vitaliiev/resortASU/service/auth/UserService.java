package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.entity.auth.User;
import vitaliiev.resortASU.repository.auth.UserRepository;

import java.util.*;

@Slf4j
@Service
public class UserService implements UserDetailsService {

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

    public boolean addUser(User user) {
        try {
            loadUserByUsername(user.getUsername());
            return false;
        } catch (UsernameNotFoundException e) {
            user.setRoles(Collections.singleton(roleService.getUser()));
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return true;
        }
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
//        return userRepository.getReferenceById(id);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateUser(User user) {
        User userFromDb = userRepository.getReferenceById(user.getId());
        if (userIsLastAdmin(userFromDb) && !user.getRoles().contains(roleService.getAdmin())) { // do not allow
            // delete admin role from last admin
            user.getRoles().add(roleService.getAdmin());
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
        userFromDb.setRoles(user.getRoles());
        return userRepository.save(userFromDb);
    }

    public User changePassword(User user) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        User userFromDb = optionalUser.orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userFromDb.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(userFromDb);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findUsersById(id);
        if (userIsLastAdmin(user)) {// do not allow to delete last user with role "admin"
            log.warn("Can't delete the only remaining user with admin rights");
        } else {
            userRepository.deleteById(id);
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String username, String roles) {
        // todo
//        List<User> userList = new ArrayList<>();
//        if (username == null || username.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        User user = userRepository.findByUsername(username);
//        if (user == null) {
//
//        }
//        List<Role> roleList = rolesService.findRoleByNames(roles.split("\\s"));
//        if (username != null && !username.isEmpty()) {
//            user.setUsername(username);
//        }
//        if (roles != null && !roles.isEmpty()) {
//
//            user.setRoles(r);
//        }
//        return userRepository.findAll(Example.of(user));
        return null;
    }

    private boolean userIsAdmin(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getName)
                .anyMatch(r -> r.equals(roleService.getAdmin().getName()));
    }

    private boolean userIsLastAdmin(User user) {
        if (userIsAdmin(user)) {
            Role admin = roleService.findRoleById(roleService.getAdmin().getId());
            return admin.getUsers().size() <= 1;
        }
        return false;
    }
}
