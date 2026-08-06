package com.iftrue.hub.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {

    boolean existsByNameAndDeletedAtIsNull(String name);
}
