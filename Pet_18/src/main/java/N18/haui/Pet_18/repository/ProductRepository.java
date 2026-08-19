package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByNameAndDeleteFlagFalse(String name);

    Optional<Product> findByIdAndDeleteFlagFalse(Long id);
}
