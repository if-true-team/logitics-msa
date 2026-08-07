package com.iftrue.delivery.domain.deliverymanager;

public interface DeliveryManagerRepository {
    
    DeliveryManager save(DeliveryManager deliveryManager);

    DeliveryManager findById(Long id);
}
