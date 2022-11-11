package vitaliiev.resortASU.model.facilities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class BuildingFloor {
    public static final String ENTITY_NAME = "BuildingFloor";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private Building building;
    @ManyToOne
    private Floor floor;


}
