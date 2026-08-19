package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long>, JpaSpecificationExecutor<Inventory> {

    @Query("SELECT i FROM Inventory i WHERE i.product.id = ?1")
    Optional<Inventory> findByProductId(Long productId);
}
