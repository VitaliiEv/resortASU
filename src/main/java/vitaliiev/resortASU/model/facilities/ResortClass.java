package vitaliiev.resortASU.model.facilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class ResortClass {
    public static final String ENTITY_NAME = "ResortClass";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Integer id;
    private String resortClass;
    private String description;
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "resortClass")
    private Set<Resort> resorts;
}
