package grocery.store.delivery.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long customerId;
	
	private String customerFirstName;
	private String customerLastName;
	private String customerEmail;
	private String customerPassword;
	
	//--need to join customers to stores, and orders to customers--

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToMany(mappedBy = "customers" , cascade = CascadeType.PERSIST)
	private Set<Store> stores = new HashSet<>();
	
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
//	@OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE, orphanRemoval = true)
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<DeliveryOrder> deliveryOrders = new HashSet<>(); //Not sure if I need this, keeping it for now
	//-------------------------------------------------------------
}
