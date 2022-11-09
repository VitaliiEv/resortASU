package vitaliiev.resortASU.model.customer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.reserve.Reserve;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Customer {
    public static final String ENTITY_NAME = "Customer";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @EqualsAndHashCode.Include
    private String firstname;
    @EqualsAndHashCode.Include
    private String surname;
    @EqualsAndHashCode.Include
    private String middlename;
    @EqualsAndHashCode.Include
    private String phone;
    @EqualsAndHashCode.Include
    private String email;
    @EqualsAndHashCode.Include
    private Date birthdate;
    @ManyToOne
    private Gender gender_id;
    @ManyToOne
    private Appeal appeal_id;
    @EqualsAndHashCode.Include
    private String adress;
    private Timestamp lastchanged;
    @OneToMany
    private Set<Document> documents;
    @ManyToMany(mappedBy = "customers")
    private Set<Reserve> reserves;
}
