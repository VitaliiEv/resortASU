package vitaliiev.resortASU.model.facilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.AbstractResortASUEntity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ResortType extends AbstractResortASUEntity/*<Integer>*/ {
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
