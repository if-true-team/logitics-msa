package com.iftrue.delivery.domain.deliveryroute;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "p_delivery_route",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_route_sequence",
                        columnNames = {"delivery_id", "sequence"}
                )
        }
)
@Getter
public class DeliveryRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Column(name = "hub_delivery_manager_id")
    private UUID hubDeliveryManagerId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private HubDeliveryStatus status;

    @Column(name = "departed_at")
    private Instant departed_at;

    @Column(name = "arrived_at")
    private Instant arrived_at;

    @Column(name = "expected_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal expected_distance;

    @Column(name = "expected_duration", nullable = false)
    private Integer expected_duration;

    @Column(name = "actual_distance", precision = 10, scale = 2)
    private BigDecimal actual_distance;

    @Column(name = "actual_duration")
    private Integer actual_duration;


}
