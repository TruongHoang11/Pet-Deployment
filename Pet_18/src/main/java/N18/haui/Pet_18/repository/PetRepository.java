package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {
    Optional<Pet> findByUserId(String userId);

    List<Pet> findByUserIdAndDeleteFlagFalse(String userId);

    Optional<Pet> findByIdAndDeleteFlagFalse(Long id);

    boolean existsByUserIdAndNameAndDeleteFlagFalse(String userId, String petName);
}