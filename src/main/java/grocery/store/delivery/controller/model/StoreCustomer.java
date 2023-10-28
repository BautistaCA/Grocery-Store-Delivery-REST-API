package grocery.store.delivery.controller.model;

import java.util.HashSet;
import java.util.Set;

import grocery.store.delivery.entity.Customer;
import grocery.store.delivery.entity.DeliveryOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StoreCustomer {
	private Long customerId;
	private String customerFirstName;
	private String customerLastName;
	private String customerEmail;
	private String customerPassword;
	private Set<CustomerDeliveryOrder> deliveryOrders = new HashSet<>();

	// ----adding this to see if anything changes----
	// Edit: Didn't need this
	// private Set<StoreData> stores = new HashSet<>();

	// ----------------------------------------------
	public StoreCustomer(Customer customer) {
		customerId = customer.getCustomerId();
		customerFirstName = customer.getCustomerFirstName();
		customerLastName = customer.getCustomerLastName();
		customerEmail = customer.getCustomerEmail();
		customerPassword = customer.getCustomerPassword();

		for (DeliveryOrder order : customer.getDeliveryOrders()) {
			deliveryOrders.add(new CustomerDeliveryOrder(order));
		}
		// not sure if this works
//		   Edit: don't think I need this
		// for(Store store : customer.getStores()) {
//			stores.add(new StoreData(store));
//		}
	}
}
