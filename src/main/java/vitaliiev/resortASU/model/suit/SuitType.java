package vitaliiev.resortASU.model.suit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.Photo;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class SuitType {
    public static final String ENTITY_NAME = "SuitType";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name="suitclass")
    @EqualsAndHashCode.Include
    private SuitClass suitClass;
    private Timestamp lastchanged;
    @ManyToOne
    @JoinColumn(name="beds")
    @EqualsAndHashCode.Include
    private Beds beds;
    @EqualsAndHashCode.Include
    private Double area;
    @EqualsAndHashCode.Include
    private BigDecimal currentprice;
    @EqualsAndHashCode.Include
    private BigDecimal minimumprice;
    @ManyToOne
    @JoinColumn(name = "mainphoto")
    private Photo mainphoto;
//    @ManyToMany
//    @JoinTable(name = "suitfeatures", joinColumns = @JoinColumn(name = "features_id"),
//        inverseJoinColumns = @JoinColumn(name = "suittype_id"))
//    private Set<Features> features;
    @ManyToMany
    @JoinTable(name = "suitservices", joinColumns = @JoinColumn(name = "services_id"),
            inverseJoinColumns = @JoinColumn(name = "suittype_id"))
    private Set<Services> services;
    private Boolean deleted = false;
    @OneToMany(mappedBy = "suittype")
    private Set<Suit> suits;
}
