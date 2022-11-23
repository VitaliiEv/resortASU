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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class RoleService {
    private final static String NAME_PREFIX = "ROLE_";
    private final static String DEFAULT_ADMIN = NAME_PREFIX + "ADMIN";
    private final static String DEFAULT_USER = NAME_PREFIX + "USER";
    private final static String DEFAULT_CONTENT_MANAGER = NAME_PREFIX + "CONTENT_MANAGER";
    private final static String DEFAULT_PROPERTY_MANAGER = NAME_PREFIX + "PROPERTY_MANAGER";
    private final static Set<String> predefinedRoles = new HashSet<>();

    static {
        predefinedRoles.add(DEFAULT_ADMIN);
        predefinedRoles.add(DEFAULT_USER);
        predefinedRoles.add(DEFAULT_CONTENT_MANAGER);
        predefinedRoles.add(DEFAULT_PROPERTY_MANAGER);
    }
    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
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
    public Role create(Role role) throws DataIntegrityViolationException {
        String name = role.getName().toUpperCase().trim();
        if (!name.startsWith(NAME_PREFIX)) {
            log.info("Expected role name with prefix ROLE_. Got {}", name);
            name = NAME_PREFIX + name;
            log.warn("Adding prefix automatically. New role name: {}", name);
        }
        role.setName(name);
        return roleRepository.save(role);
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
                .orElse(null);
    }

    public List<Role> find(Role role) {
        ExampleMatcher newSearchConditions;
        if (role.getEnabled() == null) {
            newSearchConditions = SEARCH_CONDITIONS_MATCH_ALL.withIgnorePaths("enabled");
        } else {
            newSearchConditions = SEARCH_CONDITIONS_MATCH_ALL
                    .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<Role> example = Example.of(role, newSearchConditions);
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
        if (!role.getEnabled() && (predefinedRoles.contains(role.getName()))) {
            log.warn("Can't disable built in predefined role {}.", role.getName());
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
        Role role = this.findRoleById(id); // for maximum cache use
        if (role != null) {
            if (predefinedRoles.contains(role.getName())) {
                log.warn("Cant delete built in predefined role {}.", role.getName());
            } else {
                try {
                    role.getUsers().forEach(u -> u.removeRole(role));
                    roleRepository.deleteById(id);
                } catch (DataIntegrityViolationException e) {
                    log.warn(e.getMessage());
                }
            }
        }
    }

    public Role getAdmin() {
        return findRoleByName(DEFAULT_ADMIN);
    }

    public Role getUser() {
        return findRoleByName(DEFAULT_USER);
    }

}
