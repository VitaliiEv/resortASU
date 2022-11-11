package vitaliiev.resortASU.model.facilities;

import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.Photo;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
public class Resort {

    public static final String ENTITY_NAME = "Resort";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @ManyToOne(optional = false)
    @JoinColumn(name = "resorttype")
    private ResortType resorttype;
    @ManyToOne(optional = false)
    @JoinColumn(name = "resortclass")
    private ResortClass resortClass;
    @ManyToOne
    @JoinColumn(name = "photo")
    private Photo photo;
    @OneToMany(mappedBy = "resort")
    private Set<Building> buildings;
    private Timestamp lastChanged;
    private Boolean deleted = false;

    public void removeBuilding(Building building) {
        this.buildings.remove(building);
    }
}
