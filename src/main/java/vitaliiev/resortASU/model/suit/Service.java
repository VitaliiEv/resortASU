package vitaliiev.resortASU.model.suit;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.reserve.ReserveServices;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Service {
    public static final String ENTITY_NAME = "Service";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @EqualsAndHashCode.Include
    private String service;
    private String description;
    private BigDecimal currentprice;
    private BigDecimal minimumprice;
    private Boolean onetimepayment;
    private Timestamp lastchanged;
    private Boolean deleted = false;
    @ManyToMany(mappedBy = "services")
    private Set<SuitType> suitTypes;
    @OneToMany
    private Set<ReserveServices> reserveServices;

}
