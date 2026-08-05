package com.iftrue.delivery.domain.delivery;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_delivery")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", unique = true, nullable = false)
    private UUID orderId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "destination_hub_id", nullable = false)
    private UUID destinationHubId;

    @Column(name = "company_delivery_manager_id", nullable = false)
    private UUID companyDeliveryManagerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_slack_id", nullable = false)
    private String recipientSlackId;

    // TODO : 감시로그 필드는 base만들어서 사용


    public static Delivery create() {
        return new Delivery();
    }


}
