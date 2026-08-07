package com.iftrue.delivery.domain.deliverymanager;

import java.util.Optional;

public interface DeliveryManagerRepository {

    DeliveryManager save(DeliveryManager deliveryManager);

    Optional<DeliveryManager> findById(Long id);
}
