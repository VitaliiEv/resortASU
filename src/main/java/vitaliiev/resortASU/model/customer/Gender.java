package vitaliiev.resortASU.model.customer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Gender {
    public static final String ENTITY_NAME = "Gender";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @EqualsAndHashCode.Include
    private String gender;
    @OneToMany
    private Set<Customer> customers;
}
