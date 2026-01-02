package com.razdeep.konsignapi.repository;

import com.razdeep.konsignapi.entity.KonsignUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KonsignUserRepository extends JpaRepository<KonsignUser, Integer> {
    Optional<KonsignUser> findKonsignUserByUsername(String username);
}
