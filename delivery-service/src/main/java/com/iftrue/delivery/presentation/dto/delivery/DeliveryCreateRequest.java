package com.iftrue.delivery.presentation.dto.delivery;

import com.iftrue.delivery.application.dto.delivery.DeliveryCreateCommand;

public record DeliveryCreateRequest() {
    public DeliveryCreateCommand toCommand() {
        return new DeliveryCreateCommand();
    }
}
