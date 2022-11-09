package vitaliiev.resortASU.model.customer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class Document {
    public static final String ENTITY_NAME = "Document";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private DocumentType documentType;
    @EqualsAndHashCode.Include
    private String series;
    @EqualsAndHashCode.Include
    private String number;
    private Date issued;
    private Date expired;
    @ManyToOne
    private Customer customer;
    private Timestamp lastchanged;

}
