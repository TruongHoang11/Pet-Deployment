package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.PetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetServiceRepository extends JpaRepository<PetService, Long>, JpaSpecificationExecutor<PetService> {

    Page<PetService> findByDeleteFlagFalseAndActiveFlagTrue(Pageable pageable);

    Page<PetService> findByDeleteFlagFalseAndActiveFlagTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<PetService> findByDeleteFlagFalseAndActiveFlagTrueAndCategoryId(Long categoryId, Pageable pageable);

    List<PetService> findByDeleteFlagFalseAndActiveFlagTrueAndCategoryId(Long categoryId);

    java.util.Optional<PetService> findByIdAndDeleteFlagFalseAndActiveFlagTrue(Long id);
}
