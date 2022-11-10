package vitaliiev.resortASU.model.suit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Features {
    public static final String ENTITY_NAME = "Feature";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @EqualsAndHashCode.Include
    private String feature;
    private String description;
    private Timestamp lastchanged;
    private Boolean deleted = false;
    @ManyToMany(mappedBy = "features")
    private Set<SuitType> suitTypes;

}
