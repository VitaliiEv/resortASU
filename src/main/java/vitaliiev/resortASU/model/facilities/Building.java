package vitaliiev.resortASU.model.facilities;

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
public class Building {
    public static final String ENTITY_NAME = "Building";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Include
    private String description;
    private Timestamp lastChanged;
    @ManyToOne
    @JoinColumn(name = "resort")
    private Resort resort;
    @OneToMany
    private Set<BuildingFloor> buildingFloors;

    public void removeBuildingFloor(BuildingFloor buildingFloor) {
        this.buildingFloors.remove(buildingFloor);
    }
}
