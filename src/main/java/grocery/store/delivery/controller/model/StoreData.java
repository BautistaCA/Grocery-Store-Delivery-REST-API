package grocery.store.delivery.controller.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import grocery.store.delivery.entity.Customer;
import grocery.store.delivery.entity.Store;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class StoreData {
	private Long storeId;
	private String storeName;
	private String storeAddress;
	private String storeCity;
	private String storeState;
	private String storeZip;
	private String storePhone;
	private Map<String, String> storeInventory = new HashMap<>();
	private Set<StoreCustomer> customers = new HashSet<>();
	
	public StoreData(Store store) {
		storeId = store.getStoreId();
		storeName = store.getStoreName();
		storeAddress = store.getStoreAddress();
		storeCity = store.getStoreCity();
		storeState = store.getStoreState();
		storeZip = store.getStoreZip();
		storePhone = store.getStorePhone();
		
		//need to add a Map, dont't think this will work, might need some help
		//Edit: I believe this worked fine
		storeInventory.putAll(store.getStoreInventory());
		
		//Not sure if this works, customerId doesn't get pulled in certain CRUD operations
		for(Customer customer : store.getCustomers()) {
		customers.add(new StoreCustomer(customer));
		}
	}
}
