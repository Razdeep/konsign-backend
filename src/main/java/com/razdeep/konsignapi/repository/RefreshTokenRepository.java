package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.RefreshTokenEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    //    @Modifying
    //    @Query("update RefreshToken r set r.revoked = true where r.userId = :userId")
    //    void revokeAllForUser(Long userId);
}
