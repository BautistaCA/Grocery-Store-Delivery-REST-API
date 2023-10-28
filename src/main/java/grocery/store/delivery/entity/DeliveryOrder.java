package grocery.store.delivery.entity;

import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
public class DeliveryOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long deliveryOrderId;

	private String deliveryOrderShippingAddress;
	private String deliveryOrderShippingCity;
	private String deliveryOrderShippingState;
	private String deliveryOrderShippingZip;
	private String deliveryOrderTotal;

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@CollectionTable(name = "delivery_order_groceries", joinColumns = @JoinColumn(name = "delivery_order_id"))
	@Column(name = "delivery_order_groceries_list")
	private List<String> orderGroceries = new ArrayList<>();

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	// I think removing the cascade type/join column fixed the issue of not being
	// able to delete deliveryOrders, will look over it some more
	@ManyToOne // (cascade = CascadeType.ALL)
//	@JoinColumn(name = "customer_id")
	private Customer customer; // not sure if this is needed
}
