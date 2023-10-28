package grocery.store.delivery.controller.model;

import java.util.ArrayList;
import java.util.List;

import grocery.store.delivery.entity.DeliveryOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerDeliveryOrder {
	private Long deliveryOrderId;	
	private String deliveryOrderShippingAddress;
	private String deliveryOrderShippingCity;
	private String deliveryOrderShippingState;
	private String deliveryOrderShippingZip;
	private String deliveryOrderTotal;
	private List<String> orderGroceries = new ArrayList<>();
	
	public CustomerDeliveryOrder(DeliveryOrder deliveryOrder) {
		deliveryOrderId = deliveryOrder.getDeliveryOrderId();	
		deliveryOrderShippingAddress = deliveryOrder.getDeliveryOrderShippingAddress();
		deliveryOrderShippingCity = deliveryOrder.getDeliveryOrderShippingCity();
		deliveryOrderShippingState = deliveryOrder.getDeliveryOrderShippingState();
		deliveryOrderShippingZip = deliveryOrder.getDeliveryOrderShippingZip();
		deliveryOrderTotal = deliveryOrder.getDeliveryOrderTotal();
		
		for(String orderGrocery : deliveryOrder.getOrderGroceries()) {
			orderGroceries.add(orderGrocery);
		}
	}
}
