package vitaliiev.resortASU.model.facilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ResortType {
    public static final String ENTITY_NAME = "ResortType";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Integer id;
    private String name;
    private String description;
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "resorttype")
    private Set<Resort> resorts;
}
