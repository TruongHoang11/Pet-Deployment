package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByNameAndDeleteFlagFalse(String name);
    boolean existsByIdAndDeleteFlagFalse(Long id);

    List<Category> findByDeleteFlagFalse();
}
