package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.PetServiceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetServiceReviewRepository extends JpaRepository<PetServiceReview, Long> {

    Page<PetServiceReview> findByPetServiceId(Long serviceId, Pageable pageable);

    List<PetServiceReview> findByPetServiceId(Long serviceId);

    Optional<PetServiceReview> findByPetServiceIdAndUserId(Long serviceId, String userId);

    @Query("SELECT AVG(r.rating) FROM PetServiceReview r WHERE r.petService.id = :serviceId")
    Double getAverageRating(@Param("serviceId") Long serviceId);

    @Query("SELECT COUNT(r) FROM PetServiceReview r WHERE r.petService.id = :serviceId")
    Integer getReviewCount(@Param("serviceId") Long serviceId);
}
