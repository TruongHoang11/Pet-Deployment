package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.PetServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetServiceImageRepository extends JpaRepository<PetServiceImage, Long> {

    List<PetServiceImage> findByPetServiceId(Long serviceId);

    Optional<PetServiceImage> findByPetServiceIdAndIsThumbnailTrue(Long serviceId);
}
