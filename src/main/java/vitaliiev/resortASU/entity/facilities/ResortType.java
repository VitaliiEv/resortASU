package vitaliiev.resortASU.entity.facilities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Getter
@Setter
public class ResortType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Timestamp lastchanged;
    @OneToMany(mappedBy = "resorttype")
    private Collection<Resort> resorts;
}
