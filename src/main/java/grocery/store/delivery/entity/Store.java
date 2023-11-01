package grocery.store.delivery.entity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class Store {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long storeId;

	private String storeName;
	private String storeAddress;
	private String storeCity;
	private String storeState;
	private String storeZip;
	private String storePhone;

	// Testing Something else
//	@ManyToMany(cascade = CascadeType.ALL)
//	private Map<String, String> storeInventory = new HashMap<String, String>();

	@ElementCollection
	@CollectionTable(name = "store_inventory", joinColumns = @JoinColumn(name = "store_id"))
	@MapKeyColumn(name = "grocery_item")
	@Column(name = "grocery_item_price")
	private Map<String, String> storeInventory; // = new HashMap<String, Long>();

	// Gonna switch things around
//	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)//Needed this to be able to delete child with parent

//----------
//	Was having an issue with being able to use PUT on a customer, would get a Duplicate key error. I'm not sure what exactly causes this, but
//	switching back to this solved it. Customer data is still in the database when a store is deleted, but StoreCustomer data is deleted, so 
//	everything basically still works as intended.
	@ManyToMany(cascade = CascadeType.PERSIST) // changed this from Persist to Remove, didn't work
//----------	

	@JoinTable(name = "store_customer", joinColumns = @JoinColumn(name = "store_id"), inverseJoinColumns = @JoinColumn(name = "customer_id"))
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private Set<Customer> customers = new HashSet<>();
}
