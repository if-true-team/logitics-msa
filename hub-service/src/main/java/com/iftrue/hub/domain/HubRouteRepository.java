package com.iftrue.hub.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HubRouteRepository extends JpaRepository<HubRoute, UUID> {

    boolean existsByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
            UUID departureHubId, UUID arrivalHubId
    );

    Optional<HubRoute> findByIdAndDeletedAtIsNull(UUID id);

    Page<HubRoute> findAllByDeletedAtIsNull(Pageable pageable);
}
