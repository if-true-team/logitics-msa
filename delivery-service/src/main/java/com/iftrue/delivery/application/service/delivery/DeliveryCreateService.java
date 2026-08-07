package com.iftrue.delivery.application.service.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;
import com.iftrue.delivery.application.dto.delivery.DeliveryCreateResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryCreateService {


    @Transactional
    public DeliveryCreateResult create(DeliveryCreateCommand command) {
        return new DeliveryCreateResult(null);
    }
}
