package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.PetService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<PetService, Long>, JpaSpecificationExecutor<PetService> {
    boolean existsByNameAndDeleteFlagFalse(String name);
}