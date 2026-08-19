package N18.haui.Pet_18.repository;


import N18.haui.Pet_18.domain.entity.User;
import N18.haui.Pet_18.exception.NotFoundException;
import N18.haui.Pet_18.security.UserPrincipal;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {


    Optional<User> findByEmailAndDeleteFlagFalse(String email);


    Optional<User> findByIdAndDeleteFlagFalse(String id);

    default User getUser(UserPrincipal currentUser) {
        return findByEmailAndDeleteFlagFalse(currentUser.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found with email: " + currentUser.getUsername()));
    }

    boolean existsByEmailAndDeleteFlagFalse(String email);


    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role LEFT JOIN FETCH u.role.permissions WHERE u.id = :id AND u.deleteFlag = false")
    Optional<User> findByIdWithFullInfor(@Param("id") String id);
}
