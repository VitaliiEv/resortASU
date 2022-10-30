package vitaliiev.resortASU.model.auth;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of={"username"})
@Table(name = "\"user_resortASU\"")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 5, max = 45, message = "Your username must be 5-45 characters long")
    private String username;
    @Size(min = 5, message = "Your password must be minimum 5 characters long.")
    private String password;
    private Boolean enabled = true;
    @Transient
    private String passwordConfirm;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "\"userrole\"",
            joinColumns = @JoinColumn(name = "\"user_resortASU\"", referencedColumnName = "\"id\""),
            inverseJoinColumns = @JoinColumn(name = "\"role_resortASU\"", referencedColumnName = "\"id\""))
    private Set<Role> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void addRole(Role role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            role.getUsers().add(this);
        }
    }

    public void removeRole(Role role) {
        if (this.roles.contains(role)) {
            this.roles.remove(role);
            role.getUsers().remove(this);
        }
    }

    public void addRoles(Set<Role> roles) {
        roles.forEach(this::addRole);
    }

    public void removeRoles(Set<Role> roles) {
        roles.forEach(this::removeRole);
    }
}
