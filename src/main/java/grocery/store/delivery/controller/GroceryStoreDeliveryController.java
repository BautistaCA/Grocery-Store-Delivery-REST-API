package grocery.store.delivery.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import grocery.store.delivery.controller.model.CustomerDeliveryOrder;
import grocery.store.delivery.controller.model.StoreCustomer;
import grocery.store.delivery.controller.model.StoreData;
import grocery.store.delivery.service.GroceryStoreDeliveryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/grocery_store_delivery")
@Slf4j
public class GroceryStoreDeliveryController {
	// 10/23 Most issues should be resolved, should be over halfway done from this
	// point.
	// 10/24 started on the DELETE requests
	// Tested READ requests, store/id/customer/id only pulls from store 1 for some
	// reason
	// Edit: Fixed READ requests
	// Issue: Can't delete customer, child rows not deleted.
	// 10/25: Inventory DELETE works, still need to fix customer/deliveryOrder
	// DELETE requests
	// Edit: I believe I've fixed both issues, gonna delete the database and start
	// fresh to make sure. Should probably create a mock test if I have time for it.
	// ---List of To Do's---
	// -Create-
	// Store(Done), Customer(Done), Inventory(Don't need*), DeliveryOrder(Done)
	// --Read
	// Store(Done), Customer(Issues), Inventory(Done), DeliveryOrder(Done)
	// --Update
	// Store(Done), Customer(Done), Inventory(Done)
	// --Delete
	// Store(Done), Customer(Done), Inventory(Done), DeliveryOrder(Done)
	// *Didn't need since an empty Inventory is created when a store is created.
	//----------------------
	// As of 10/25, this project should be in a full working state that meets all
	// the necessary requirements for submission and review.

	@Autowired
	private GroceryStoreDeliveryService groceryStoreDeliveryService;

	@PostMapping("/store")
	@ResponseStatus(code = HttpStatus.CREATED)
	public StoreData createStoreData(@RequestBody StoreData storeData) {
		log.info("Creating Store {}", storeData);
		return groceryStoreDeliveryService.saveStore(storeData);
	}

	@PutMapping("/store/{storeId}")
	public StoreData updateStoreData(@PathVariable Long storeId, @RequestBody StoreData storeData) {
		storeData.setStoreId(storeId);
		log.info("Updating Store {}, with {}", storeId, storeData);
		return groceryStoreDeliveryService.saveStore(storeData);
	}

	@PostMapping("/store/{storeId}/customer")
	@ResponseStatus(code = HttpStatus.CREATED)
	public StoreCustomer addCustomer(@PathVariable Long storeId,
			@RequestBody StoreCustomer storeCustomer) {
		log.info("Creating new Customer info at Store {}", storeId);
		return groceryStoreDeliveryService.saveCustomer(storeId, storeCustomer);
	}

	// customerId isn't being pulled for some reason, need to fix it, 90% of issues
	// stem from this
	// Edit: I forgot to annotate customerId as another @PathVariable, gotta
	// remember to do this.
	@PutMapping("/store/{storeId}/customer/{customerId}")
	public StoreCustomer updateCustomer(@PathVariable Long storeId, @PathVariable Long customerId,
			@RequestBody StoreCustomer storeCustomer) {

		storeCustomer.setCustomerId(customerId);
		log.info("Updating Customer {} from Store {}", customerId, storeId);
		return groceryStoreDeliveryService.saveCustomer(storeId, storeCustomer);
	}

	@PutMapping("/store/{storeId}/store_inventory")
	public StoreData createStoreInventory(@PathVariable Long storeId,
			@RequestBody StoreData storeData) {
		storeData.setStoreId(storeId);
		log.info("Creating new Inventory at Store {} : {}", storeId, storeData.getStoreInventory());
		return groceryStoreDeliveryService.addStoreInventory(storeId, storeData);
	}

	// This might not be correct, gonna find out if its POST or PUT
	// 10/23 only pulls the storeId, doesn't pull customerId for some reason
	// Edit: fixed issue, should probably redo this method though
	// Edit: Everything works as intended.
	@PostMapping("/store/{storeId}/customer/{customerId}/delivery_order")
	@ResponseStatus(code = HttpStatus.CREATED)
	public CustomerDeliveryOrder createOrder(@PathVariable Long storeId,
			@PathVariable Long customerId, StoreCustomer storeCustomer, StoreData storeData,
			@RequestBody CustomerDeliveryOrder customerDeliveryOrder) {
//		storeData.setStoreId(storeId);
//		storeCustomer.setCustomerId(customerId);
		log.info("Order from Customer {} added to Store {}", customerId, storeId);
		return groceryStoreDeliveryService.saveDeliveryOrder(storeId, customerId,
				customerDeliveryOrder);
	}
	// --Decided to keep the GETs and DELETEs bunched together for organization--

	@GetMapping("/store")
	public List<StoreData> retreiveAllStores() {
		log.info("Listing All Stores");
		return groceryStoreDeliveryService.retreiveAllStores();
	}

	@GetMapping("/store/{storeId}")
	public StoreData retrieveStoreById(@PathVariable Long storeId) {
		log.info("Retrieving data from Store {}", storeId);
		return groceryStoreDeliveryService.retreiveStoreById(storeId);
	}

	@GetMapping("/store/{storeId}/customer")
	public List<StoreCustomer> retrieveAllCustomers(@PathVariable Long storeId) {
		log.info("Listing all Customers from Store {}", storeId);
		return groceryStoreDeliveryService.retreiveAllCustomers(storeId);
	}

	// Doesn't work right now, doesn't pull customer info from store for some reason
	// Fixed issue, similar issue to others above this
	@GetMapping("/store/{storeId}/customer/{customerId}")
	public StoreCustomer retrieveCustomerById(@PathVariable Long storeId,
			@PathVariable Long customerId) {
		log.info("Retreiving data of Customer {} from Store {}", customerId, storeId);
		return groceryStoreDeliveryService.retreiveCustomerById(storeId, customerId);
	}

	// 10/24 Didn't think this would work, but it works perfectly fine
	@GetMapping("/store/{storeId}/store_inventory")
	public Map<String, String> retrieveStoreInventory(@PathVariable Long storeId) {
		log.info("Retrieving Store Inventory from Store {}", storeId);
		return groceryStoreDeliveryService.retreiveStoreInventory(storeId);
	}

	@GetMapping("/store/{storeId}/customer/{customerId}/delivery_order/{deliveryOrderId}")
	public CustomerDeliveryOrder retrieveDeliveryOrderById(@PathVariable Long storeId,
			@PathVariable Long customerId, @PathVariable Long deliveryOrderId) {
		log.info("Retrieving Data of Delivery Order {} from Customer {} at Store {}",
				deliveryOrderId, customerId, storeId);
		return groceryStoreDeliveryService.retreiveDeliveryOrderById(storeId, customerId,
				deliveryOrderId);
	}
	// making all the DELETE requests the same, should probably refactor them at
	// some point.

	@DeleteMapping("/store/{storeId}/customer/{customerId}/delivery_order/{deliveryOrderId}")
	public Map<String, String> deleteDeliveryOrderById(@PathVariable Long storeId,
			@PathVariable Long customerId, @PathVariable Long deliveryOrderId) {
		Map<String, String> deleteMessage = new HashMap<>();
		log.info("Deleting Delivery Order {} made by Customer {} at Store {}", deliveryOrderId,
				customerId, storeId);
		groceryStoreDeliveryService.deleteDeliveryOrderById(storeId, customerId, deliveryOrderId);

		deleteMessage.put("Message", "Deletion Successful");

		return deleteMessage;
	}

	@DeleteMapping("/store/{storeId}/customer/{customerId}")
	public Map<String, String> deleteCustomerById(@PathVariable Long storeId,
			@PathVariable Long customerId) {
		Map<String, String> deleteMessage = new HashMap<>();
		log.info("Deleting Customer {} at Store {}", customerId, storeId);
		groceryStoreDeliveryService.deleteCustomerById(storeId, customerId);

		deleteMessage.put("Message", "Deletion Successful");

		return deleteMessage;
	}

	// Works
	@DeleteMapping("/store/{storeId}")
	public Map<String, String> deleteStoreById(@PathVariable Long storeId) {
		Map<String, String> deleteMessage = new HashMap<>();
		log.info("Deleting Store {}", storeId);
		groceryStoreDeliveryService.deleteStoreById(storeId);

		deleteMessage.put("Message", "Deletion Successful");

		return deleteMessage;
	}

	@DeleteMapping("/store/{storeId}/store_inventory")
	public Map<String, String> deleteStoreInventoryById(@PathVariable Long storeId) {
		Map<String, String> deleteMessage = new HashMap<>();
		log.info("Deleting Inventory of Store {}", storeId);
		groceryStoreDeliveryService.deleteStoreInventoryById(storeId);

		deleteMessage.put("Message", "Deletion Successful");

		return deleteMessage;
	}
}
