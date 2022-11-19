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
    @Column(name = "beds_adult")
    private Integer beds_adult;
    @EqualsAndHashCode.Include
    @Column(name = "beds_child")
    private Integer beds_child;
    @OneToMany(mappedBy = "beds")
    private Set<SuitType> suitTypes;

    public boolean quantityEquals(Beds bed) {
        return this.beds_adult.equals(bed.getBeds_adult()) && this.beds_child.equals(bed.getBeds_child());
    }

}
