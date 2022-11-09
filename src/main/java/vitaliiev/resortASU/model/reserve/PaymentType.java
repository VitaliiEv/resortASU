package vitaliiev.resortASU.model.reserve;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class PaymentType {
    public static final String ENTITY_NAME = "PaymentType";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @EqualsAndHashCode.Include
    private String paymenttype;
    private Boolean enabled = true;
    @OneToMany
    private Set<Reserve> reserves;
}
