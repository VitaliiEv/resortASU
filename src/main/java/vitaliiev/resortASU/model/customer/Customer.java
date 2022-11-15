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
@EqualsAndHashCode(callSuper = false)
public class Customer {
    public static final String ENTITY_NAME = "Customer";

    @Id
    @EqualsAndHashCode.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String surname;
    private String middlename;
    private String phone;
    private String email;
    private Date birthdate;
    @ManyToOne
    private Gender gender_id;
//    @ManyToOne
//    @EqualsAndHashCode.Exclude
//    private Appeal appeal_id;
    @EqualsAndHashCode.Exclude
    private String address;
    private Timestamp lastchanged;
//    @OneToMany
//    private Set<Document> documents;
    @ManyToMany(mappedBy = "customers")
    private Set<Reserve> reserves;
}
