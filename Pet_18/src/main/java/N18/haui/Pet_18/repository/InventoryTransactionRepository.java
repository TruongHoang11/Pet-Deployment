package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long>, JpaSpecificationExecutor<InventoryTransaction> {

    InventoryTransaction findByInventoryId(Long inventoryId);

    List<InventoryTransaction> findByInventoryIdOrderByCreatedDateDesc(Long inventoryId);
}
