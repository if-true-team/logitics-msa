package com.iftrue.hub.application.dto;

import com.iftrue.hub.domain.Hub;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class HubResponseDto {

    private final UUID id;
    private final String name;
    private final String address;
    private final BigDecimal latitude;
    private final BigDecimal longitude;

    public HubResponseDto(
            UUID id,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static HubResponseDto from(Hub hub) {
        return new HubResponseDto(
                hub.getId(),
                hub.getName(),
                hub.getAddress(),
                hub.getLatitude(),
                hub.getLongitude()
        );
    }
}
