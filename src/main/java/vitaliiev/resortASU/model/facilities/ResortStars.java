package vitaliiev.resortASU.model.facilities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
public class ResortStars {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String stars;
    private String description;
    @OneToMany(mappedBy = "resortstars")
    private Collection<Resort> resorts;
}
