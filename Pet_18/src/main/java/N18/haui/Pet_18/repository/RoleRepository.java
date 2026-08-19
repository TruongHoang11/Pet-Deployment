package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByNameAndDeleteFlagFalse(String name);
    boolean existsByNameAndDeleteFlagFalse(String name);


    Optional<Role> findByIdAndDeleteFlagFalse(Long id);
    boolean existsByIdAndDeleteFlagFalse(Long id);

}
