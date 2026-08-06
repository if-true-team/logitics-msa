package com.iftrue.hub.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {

    boolean existsByNameAndDeletedAtIsNull(String name);

    Optional<Hub> findByIdAndDeletedAtIsNull(UUID id);

    Page<Hub> findAllByDeletedAtIsNull(Pageable pageable);
}
