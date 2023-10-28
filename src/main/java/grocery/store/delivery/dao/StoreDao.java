package grocery.store.delivery.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import grocery.store.delivery.entity.Store;

public interface StoreDao extends JpaRepository<Store, Long> {

}
