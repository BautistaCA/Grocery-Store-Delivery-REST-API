package grocery.store.delivery.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import grocery.store.delivery.controller.model.CustomerDeliveryOrder;
import grocery.store.delivery.controller.model.StoreCustomer;
import grocery.store.delivery.controller.model.StoreData;
import grocery.store.delivery.dao.CustomerDao;
import grocery.store.delivery.dao.DeliveryOrderDao;
import grocery.store.delivery.dao.StoreDao;
import grocery.store.delivery.entity.Customer;
import grocery.store.delivery.entity.DeliveryOrder;
import grocery.store.delivery.entity.Store;

@Service
public class GroceryStoreDeliveryService {

	@Autowired
	private StoreDao storeDao;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private DeliveryOrderDao deliveryOrderDao;

	public StoreData saveStore(StoreData storeData) {
		Long storeId = storeData.getStoreId();
		Store store = findOrCreateStore(storeId);
		copyStoreFields(store, storeData);

		return new StoreData(storeDao.save(store));
	}

	private void copyStoreFields(Store store, StoreData storeData) {
		store.setStoreName(storeData.getStoreName());
		store.setStoreAddress(storeData.getStoreAddress());
		store.setStoreCity(storeData.getStoreCity());
		store.setStoreState(storeData.getStoreState());
		store.setStoreZip(storeData.getStoreZip());
		store.setStorePhone(storeData.getStorePhone());
		store.setStoreInventory(storeData.getStoreInventory());
	}

	private Store findOrCreateStore(Long storeId) {
		Store store;
		if (Objects.isNull(storeId)) {
			store = new Store();
		} else {
			store = findByStoreId(storeId);
		}
		return store;
	}

	private Store findByStoreId(Long storeId) {
		return storeDao.findById(storeId).orElseThrow(() -> new NoSuchElementException(
				"A Store with an ID of " + storeId + " does not exist."));
	}

	@Transactional(readOnly = false)
	public StoreCustomer saveCustomer(Long storeId, StoreCustomer storeCustomer) {
		Store store = findByStoreId(storeId);
	//	Long customerId = storeCustomer.getCustomerId(); // Don't think I need this
		Customer customer = findOrCreateCustomer(storeId, storeCustomer.getCustomerId());

		copyCustomerFields(customer, storeCustomer);

		// Think this works, might need to change. Edit: didn't work
		customer.getStores().add(store); 


		store.getCustomers().add(customer);

		Customer dbCustomer = customerDao.save(customer);
		StoreCustomer dbStoreCustomer = new StoreCustomer(dbCustomer);
		
		return dbStoreCustomer;
	}

	private Customer findOrCreateCustomer(Long storeId, Long customerId) {
		Customer customer;
		if (Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findByCustomerId(storeId, customerId);
		}
		return customer;
	}

	private void copyCustomerFields(Customer customer, StoreCustomer storeCustomer) {
		customer.setCustomerFirstName(storeCustomer.getCustomerFirstName());
		customer.setCustomerLastName(storeCustomer.getCustomerLastName());
		customer.setCustomerPassword(storeCustomer.getCustomerPassword());
		customer.setCustomerEmail(storeCustomer.getCustomerEmail());
//		customer.setDeliveryOrders(null);
//		^^^don't need this but keeping if I have to add deliveries for some reason.
	}

	private Customer findByCustomerId(Long storeId, Long customerId) {
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException(
						"Valid Customer ID of" + customerId + " Not Found"));

//		List<Store> stores = storeDao.findAll();
		for (Store store : customer.getStores()) {
			if (store.getStoreId() != storeId) {
				throw new IllegalArgumentException("Store ID " + storeId + " does not match");
			} else {
				return customer;
			}
		}

		return customer;
	}

	public StoreData addStoreInventory(Long storeId, StoreData storeData) {
		Store store = findByStoreId(storeId);
		store.setStoreInventory(storeData.getStoreInventory());
		return new StoreData(storeDao.save(store));
		
//--------Kept this in just in in case I need to look over my mistakes-----
		// Store store = storeDao.findById(storeId).orElseThrow(() -> new
		// NoSuchElementException(
//				"Valid Store ID of" + storeId + " Not Found"));
//		if(store.getStoreInventory().isEmpty()) {
//			copyStoreFields(store, storeData);
//		} // might need to add an exception, need to test
//		return new StoreData(storeDao.save(store));
//-------------------------------------------------------------------------
	}

	@Transactional(readOnly = true)
	public List<StoreData> retreiveAllStores() {
		List<Store> stores = storeDao.findAll();
		List<StoreData> data = new LinkedList<>();
		for (Store store : stores) {
			StoreData storeData = new StoreData(store);
//---------might just use only customer clear to keep the result clutter-free--			
			storeData.getCustomers().clear();
			storeData.getStoreInventory().clear();
//-----------------------------------------------------------------------------			
			data.add(storeData);
		}
		return data;
	}

	@Transactional(readOnly = false)
	public CustomerDeliveryOrder saveDeliveryOrder(Long storeId, Long customerId,
			CustomerDeliveryOrder customerDeliveryOrder) {
		Store store = findByStoreId(storeId);
		Customer customer = findByCustomerId(storeId, customerId);
		Long deliveryOrderId = customerDeliveryOrder.getDeliveryOrderId();

		customerDeliveryOrder.setDeliveryOrderTotal(addDeliveryTotal(store, customerDeliveryOrder));

		DeliveryOrder deliveryOrder = findOrCreateDeliveryOrder(storeId, customerId,
				deliveryOrderId);

		copyDeliveryOrderFields(deliveryOrder, customerDeliveryOrder);

		// --not sure if i need this--
		deliveryOrder.setCustomer(customer);
		customer.getDeliveryOrders().add(deliveryOrder);
		// ---------------------------
		DeliveryOrder dbOrder = deliveryOrderDao.save(deliveryOrder);
		return new CustomerDeliveryOrder(dbOrder);
	}

	private String addDeliveryTotal(Store store, CustomerDeliveryOrder customerDeliveryOrder) {
		List<String> orderList = new ArrayList<>();
		List<String> storeInventory = new ArrayList<>();
		double total = 0;
		orderList.addAll(customerDeliveryOrder.getOrderGroceries());
		storeInventory.addAll(store.getStoreInventory().keySet());

		// this should return the price total based of the order list, items not in the
		// store inventory should reflect (Item Unavailable) in the order list when
		// read. Having this method alter the grocery list is a bit messy, but as of now
		// I couldn't think of a cleaner way to go about it. In hindsight, it'd probably
		// be better to use String builder a lot more throughout the project to prevent
		// heavy memory usage/memory leaks, but String works fine.
		for (String item : orderList) {
			if (storeInventory.contains(item)) {
				total += Double.valueOf(store.getStoreInventory().get(item));
				//total += Integer.parseInt(store.getStoreInventory().get(item));
			} else {
				StringBuilder itemUnavailable = new StringBuilder();
				itemUnavailable.append(item);
				itemUnavailable.append("(item unavailable)");
				orderList.set(orderList.indexOf(item), itemUnavailable.toString());
			}
			// this should show (item unavailable) in the groceryList, could possibly be
			// in the wrong spot, need to test it.
			customerDeliveryOrder.setOrderGroceries(orderList);
		}

		return Double.toString(total);
	}

	private void copyDeliveryOrderFields(DeliveryOrder deliveryOrder,
			CustomerDeliveryOrder customerDeliveryOrder) {

		deliveryOrder.setDeliveryOrderShippingAddress(
				customerDeliveryOrder.getDeliveryOrderShippingAddress());

		deliveryOrder
				.setDeliveryOrderShippingCity(customerDeliveryOrder.getDeliveryOrderShippingCity());

		deliveryOrder.setDeliveryOrderShippingState(
				customerDeliveryOrder.getDeliveryOrderShippingState());

		deliveryOrder
				.setDeliveryOrderShippingZip(customerDeliveryOrder.getDeliveryOrderShippingZip());

		deliveryOrder.setDeliveryOrderTotal(customerDeliveryOrder.getDeliveryOrderTotal());
		deliveryOrder.setOrderGroceries(customerDeliveryOrder.getOrderGroceries());

	}

	private DeliveryOrder findOrCreateDeliveryOrder(Long storeId, Long customerId,
			Long deliveryOrderId) {
		DeliveryOrder deliveryOrder;
		if (Objects.isNull(deliveryOrderId)) {
			deliveryOrder = new DeliveryOrder();
		} else {
			deliveryOrder = findByDeliveryOrderId(storeId, customerId, deliveryOrderId);
		}
		return deliveryOrder;
	}

	private DeliveryOrder findByDeliveryOrderId(Long storeId, Long customerId,
			Long deliveryOrderId) {
		// Might need to fix this up, gonna test 1st
		// Edit: Should be working as intended

		DeliveryOrder deliveryOrder = deliveryOrderDao.findById(deliveryOrderId)
				.orElseThrow(() -> new NoSuchElementException(
						"Valid Order ID of" + deliveryOrderId + " Not Found"));

	//	List<Customer> customers = customerDao.findAll();
		for (Customer customer : findByStoreId(storeId).getCustomers()) {
			if (customer.getCustomerId() != customerId) {
				throw new IllegalArgumentException("Customer ID " + customerId + " does not match");
			} else {
				return deliveryOrder;
			}
		}
		return deliveryOrder;
	}

	@Transactional(readOnly = true)
	public List<StoreCustomer> retreiveAllCustomers(Long storeId) {
		Store store = findByStoreId(storeId);
		// not sure this works
		// Edit: This should be working
		List<Customer> customers = new ArrayList<>(store.getCustomers());
		List<StoreCustomer> data = new LinkedList<>();

		for (Customer customer : customers) {
			StoreCustomer storeCustomer = new StoreCustomer(customer);
			data.add(storeCustomer);
		}
		return data;
	}

	@Transactional(readOnly = true)
	public StoreData retreiveStoreById(Long storeId) {
		StoreData storeData = new StoreData(findByStoreId(storeId));
		return storeData;
	}
	// only pulls from store 1 for some reason, gonna try to fix
	// Edit: fixed it, was an issue with not using the correct @PathVariable in the controller
	@Transactional(readOnly = true)
	public StoreCustomer retreiveCustomerById(Long storeId, Long customerId) {
		StoreCustomer storeCustomer = new StoreCustomer(findByCustomerId(storeId, customerId));
		return storeCustomer;
		//changing this
//		Customer customer = findByCustomerId(storeId, customerId);
//		return new StoreCustomer(customer);
	}

	@Transactional(readOnly = true)
	public Map<String, String> retreiveStoreInventory(Long storeId) {
		StoreData storeData = retreiveStoreById(storeId);
		Map<String, String> inventory = new HashMap<>();
		inventory.putAll(storeData.getStoreInventory());
		return inventory;
		
		
	}

	@Transactional(readOnly = true)
	public CustomerDeliveryOrder retreiveDeliveryOrderById(Long storeId, Long customerId,
			Long deliveryOrderId) {
		DeliveryOrder deliveryOrder = findByDeliveryOrderId(storeId, customerId, deliveryOrderId);
		return new CustomerDeliveryOrder(deliveryOrder);
	}

	@Transactional(readOnly = false)
	public void deleteDeliveryOrderById(Long storeId, Long customerId, Long deliveryOrderId) {
		deliveryOrderDao.delete(findByDeliveryOrderId(storeId, customerId, deliveryOrderId));
//------------------------------Don't need this------------------------------------------
//		Customer customer = findByCustomerId(storeId, customerId);
//		DeliveryOrder deliveryOrder = findByDeliveryOrderId(storeId, customerId, deliveryOrderId);
//		customer.getDeliveryOrders().remove(deliveryOrder);
//		customerDao.save(customer);
		//deliveryOrderDao.delete(deliveryOrder);
//---------------------------------------------------------------------------------------
	}
	
	@Transactional(readOnly = false)
	public void deleteCustomerById(Long storeId, Long customerId) {
	// This seems to be the solution to the issues I had, there might be a better way to go about this.
		Customer customer = findByCustomerId(storeId, customerId);
		Store store = findByStoreId(storeId);
		store.getCustomers().remove(customer);
		saveStore(new StoreData(store));
	// checked
		customerDao.delete(customer);
				
//		customerDao.delete(findByCustomerId(storeId, customerId));
		
	}
	
	@Transactional(readOnly = false)
	public void deleteStoreById(Long storeId) {
		storeDao.delete(findByStoreId(storeId));
		
	}

	@Transactional(readOnly = false)
	public void deleteStoreInventoryById(Long storeId) {
//	    StoreData storeData = retreiveStoreById(storeId);
//		if(storeData != null) {
//			storeData.setStoreInventory(new HashMap<>());
//		}
	//Not sure if this works
	//	storeData.getStoreInventory().clear();
		
		// This works
		Store store = findByStoreId(storeId);
		store.setStoreInventory(new HashMap<>());
		
		saveStore(new StoreData(store));
	}
}
