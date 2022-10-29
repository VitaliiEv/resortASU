package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {
    private final static String DEFAULT_ADMIN = "ROLE_ADMIN";
    private final static String DEFAULT_USER = "ROLE_USER";

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Caching(
            put = {@CachePut(cacheNames = "roles", key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = "rolesList", allEntries = true)}
    )
    public Role create(Role role) {
        String name = role.getName();
        if (!name.startsWith("ROLE_")) {
            log.info("Expected role name with prefix ROLE_. Got {}", name);
            name = "ROLE_" + name;
            role.setName(name);
            log.warn("Adding prefix automatically. New role name: {}", name);
        }
        try {
            return roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return role;
        }
    }

    @Cacheable(cacheNames = "roles", key = "#id")
    public Role findRoleById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role findRoleByName(String name) {
        List<Role> unfilteredList = this.findRoleByNames(new String[] {name});
        return unfilteredList.stream()
                .filter(r-> r.getName().equals(name))
                .findAny().orElse(null); // todo Optional
    }

    public List<Role> findRoleByNames(String[] names) {
        return this.findRoleByNames(names, true);
    }

    public List<Role> findRoleByNames(String[] names, boolean strict) {
        //todo https://www.baeldung.com/spring-data-criteria-queries

        if (names == null || names.length == 0) {
            return new ArrayList<>();
        }
        Predicate<Role> matcher;
        if (strict) {
            matcher = role -> {
                for (String roleName : names) {
                    if (role.getName().equals(roleName)) {
                        return true;
                    }
                }
                return false;
            };
        } else {
            matcher = role -> {
                for (String roleName : names) {
                    if (role.getName().contains(roleName)) {
                        return true;
                    }
                }
                return false;
            };
        }

        // assume that roles list is small, and we can fetch it quickly
        return this.findAll().stream()
                .filter(matcher)
                .collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "rolesList")
    public List<Role> findAll() {
        return roleRepository.findAll(Sort.by("name"));
//        return roleRepository.findAll();
    }

    @Caching(
            put = {@CachePut(cacheNames = "roles", key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = "rolesList", allEntries = true)}
    )
    public Role update(Role role) {
        if (!role.getEnabled() && (role.getName().equals(DEFAULT_ADMIN) || role.getName().equals(DEFAULT_USER))) {
            log.warn("Can't disable ADMIN or USER built in role.");
            role.setEnabled(true);
        }
        try {
            return roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return role;
        }
    }

    @Caching(
            evict = {@CacheEvict(cacheNames = "roles", key = "#id"),
                    @CacheEvict(cacheNames = "rolesList", allEntries = true)}
    )
    public void delete(Integer id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        optionalRole.ifPresent(r -> {
            if (r.getName().equals(DEFAULT_ADMIN) ||
                    r.getName().equals(DEFAULT_USER)) {
                log.warn("Cant delete predefined USER or ADMIN roles.");
            } else {
                roleRepository.deleteById(id);
            }
        });
    }

    public Role getAdmin() {
        return findRoleByName(DEFAULT_ADMIN);
    }

    public Role getUser() {
        return findRoleByName(DEFAULT_USER);
    }

}
