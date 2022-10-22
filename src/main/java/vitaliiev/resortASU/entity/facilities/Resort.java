package vitaliiev.resortASU.entity.facilities;

import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.entity.Photo;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Resort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // TODO: 20.10.2022 one to many 
    private String name;
    private String description;
    @ManyToOne(optional = false)
    private ResortType resorttype;
    @ManyToOne(optional = false)
    private ResortStars resortstars;
    @ManyToOne(optional = false)
    private Photo photo;
    private Timestamp lastchanged;
}
