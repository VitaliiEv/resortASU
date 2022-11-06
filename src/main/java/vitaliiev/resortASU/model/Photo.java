package vitaliiev.resortASU.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import vitaliiev.resortASU.model.facilities.Resort;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Photo {

    public static final String ENTITY_NAME = "Photo";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // TODO: 20.10.2022 many to many
    @EqualsAndHashCode.Include
    private String hash;
    @ToString.Include
    private String filename;
    private String filetype;
    private Timestamp created;
    @OneToMany(mappedBy = "photo")
    private Set<Resort> resorts;

}
