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
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RoleService {
    private final static String NAME_PREFIX = "ROLE_";
    private final static String DEFAULT_ADMIN = NAME_PREFIX + "ADMIN";
    private final static String DEFAULT_USER = NAME_PREFIX + "USER";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnorePaths("id", "users");

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
        String name = role.getName().toUpperCase();
        if (!name.startsWith(NAME_PREFIX)) {
            log.info("Expected role name with prefix ROLE_. Got {}", name);
            name = NAME_PREFIX + name;
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
        // assume that roles list is small, and we can fetch it quickly
        return this.findAll().stream()
                .filter(r-> r.getName().equals(name))
                .findAny()
                .orElse(null); // todo Optional
    }

    public List<Role> find(Role role) {
        Example<Role> example = Example.of(role, SEARCH_CONDITIONS_MATCH_ALL);
        return roleRepository.findAll(example, Sort.by("name"));
    }

    @Cacheable(cacheNames = "rolesList")
    public List<Role> findAll() {
        return roleRepository.findAll(Sort.by("name"));
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
                try {
                    roleRepository.deleteById(id);
                } catch (DataIntegrityViolationException e) {
                    log.warn(e.getMessage());
                }
            }
        });
    }

    public Role getAdmin() { //todo improve caching
        return findRoleByName(DEFAULT_ADMIN);
    }

    public Role getUser() {
        return findRoleByName(DEFAULT_USER);
    }

}
