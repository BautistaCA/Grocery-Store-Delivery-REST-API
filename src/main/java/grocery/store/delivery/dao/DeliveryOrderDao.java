package grocery.store.delivery.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import grocery.store.delivery.entity.DeliveryOrder;

public interface DeliveryOrderDao extends JpaRepository<DeliveryOrder, Long> {

}
