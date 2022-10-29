package vitaliiev.resortASU.model;

import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.facilities.Resort;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Getter
@Setter
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // TODO: 20.10.2022 many to many
    private String filename;
    private String filetype;
    private Timestamp lastchanged;
    private Boolean enabled = true;
    @OneToMany(mappedBy = "photo")
    private Collection<Resort> resorts;

}
