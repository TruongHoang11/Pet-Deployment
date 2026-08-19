package N18.haui.Pet_18.repository;

import N18.haui.Pet_18.domain.entity.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long>, JpaSpecificationExecutor<ShippingAddress> {
    long countByUserId(String userId);

    Optional<ShippingAddress> findByUserIdAndIsDefaultTrue(String userId);

    List<ShippingAddress> findByUserId(String userId);

    boolean existsByUserIdAndFullNameAndPhoneAndProvinceAndDistrictAndWardAndAddressDetail(String userId, String fullName, String phone, String province, String district, String ward, String addressDetail);
}
