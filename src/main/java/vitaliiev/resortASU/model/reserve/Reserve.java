package vitaliiev.resortASU.model.reserve;

import lombok.Getter;
import lombok.Setter;
import vitaliiev.resortASU.model.customer.Customer;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Getter
@Setter
public class Reserve {
    public static final String ENTITY_NAME = "Reserve";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date checkin;
    private Date checkout;
    @ManyToOne
    private PaymentStatus paymentstatus;
    @ManyToOne
    private PaymentType paymenttype;
    @ManyToOne
    private ReserveStatus reserveStatus;
    private Timestamp created;
    private Timestamp lastchanged;
    @ManyToMany
    @JoinTable(name = "customerreserve",
            joinColumns = @JoinColumn(name = "customer_id"))
    private Set<Customer> customers;
    @OneToMany
    private Set<ReserveSuit> reserveSuit;

    public void addCustomer(Customer customer) {
        if (this.customers.contains(customer)) {
            this.customers.add(customer);
            customer.getReserves().add(this);
        }
    }

    public void removeCustomer(Customer customer) {
        if (this.customers.contains(customer)) {
            this.customers.remove(customer);
            customer.getReserves().remove(this);
        }
    }
}
