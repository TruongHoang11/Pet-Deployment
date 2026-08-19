package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    List<Menu> findByParentIsNullOrderBySortOrderAsc();

    List<Menu> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<Menu> findByActiveFlagTrueOrderBySortOrderAsc();

}