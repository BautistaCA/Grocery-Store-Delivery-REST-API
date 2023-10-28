package grocery.store.delivery.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import grocery.store.delivery.entity.Customer;

public interface CustomerDao extends JpaRepository<Customer, Long> {

}
