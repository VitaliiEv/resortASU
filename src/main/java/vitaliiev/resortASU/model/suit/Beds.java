package vitaliiev.resortASU.model.suit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Beds {
    public static final String ENTITY_NAME = "Beds";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @EqualsAndHashCode.Include
    private String beds;
    @EqualsAndHashCode.Include
    private Integer beds_adult;
    @EqualsAndHashCode.Include
    private Integer beds_child;
    @OneToMany
    private Set<SuitType> suitTypes;

}
