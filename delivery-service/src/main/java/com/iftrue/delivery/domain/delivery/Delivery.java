package com.iftrue.delivery.domain.delivery;

import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import com.iftrue.delivery.domain.deliveryroute.DeliveryRoute;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "p_delivery",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_delivery_order_id", columnNames = "order_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "destination_hub_id", nullable = false)
    private UUID destinationHubId;

    @Column(name = "company_delivery_manager_id")
    private UUID companyDeliveryManagerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DeliveryStatus status;

    @Column(name = "delivery_address", nullable = false, length = 255)
    private String deliveryAddress;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "recipient_slack_id", nullable = false, length = 50)
    private String recipientSlackId;

    @OneToMany(
            mappedBy = "delivery",
            cascade = CascadeType.PERSIST
    )
    @OrderBy("sequence ASC")
    private List<DeliveryRoute> deliveryRoutes = new ArrayList<>();

    private Delivery(
            UUID orderId,
            UUID departureHubId,
            UUID destinationHubId,
            String deliveryAddress,
            String recipientName,
            String recipientSlackId
    ) {
        this.orderId = Objects.requireNonNull(orderId, "주문 ID는 필수입니다."); // TODO: 예외처리는 정리하여 추후 맞는 예외로 모두 수정

        this.departureHubId = Objects.requireNonNull(departureHubId, "출발 허브 ID는 필수입니다.");

        this.destinationHubId = Objects.requireNonNull(destinationHubId, "도착 허브 ID는 필수입니다.");

        this.deliveryAddress = requireText(deliveryAddress, "배송 주소는 필수입니다.");

        this.recipientName = requireText(recipientName, "수령인 이름은 필수입니다.");

        this.recipientSlackId = requireText(recipientSlackId, "수령인 Slack ID는 필수입니다.");

        this.status = DeliveryStatus.WAITING_AT_DEPARTURE_HUB;
    }

    public static Delivery create(
            UUID orderId,
            UUID departureHubId,
            UUID destinationHubId,
            String deliveryAddress,
            String recipientName,
            String recipientSlackId
    ) {

        return new Delivery(
                orderId,
                departureHubId,
                destinationHubId,
                deliveryAddress,
                recipientName,
                recipientSlackId
        );
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message); // TODO: 예외처리는 정리하여 추후 맞는 예외로 모두 수정
        }

        return value;
    }

    public void assignCompanyManager(DeliveryManager manager) {
        Objects.requireNonNull(manager, "배송담당자는 필수입니다.");

        validateCompanyManagerAssignmentStatus();
        manager.validateCompanyDeliveryAssignable(destinationHubId);

        this.companyDeliveryManagerId = manager.getId();
    }

    public void startHubDelivery() {
        if (status != DeliveryStatus.WAITING_AT_DEPARTURE_HUB) {
            throw new IllegalStateException(
                    "출발 허브 대기 상태에서만 허브 배송을 시작할 수 있습니다."
            );
        }

        this.status = DeliveryStatus.MOVING_BETWEEN_HUBS;
    }

    public void startCompanyDelivery() {
        if (status != DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
            throw new IllegalStateException(
                    "목적지 허브 도착 상태에서만 업체 배송을 시작할 수 있습니다."
            );
        }

        if (companyDeliveryManagerId == null) {
            throw new IllegalStateException(
                    "업체 배송담당자가 배정되지 않았습니다."
            );
        }

        this.status = DeliveryStatus.MOVING_TO_COMPANY;
    }

    public void arriveAtDestinationHub() {
        if (status != DeliveryStatus.MOVING_BETWEEN_HUBS) {
            throw new IllegalStateException(
                    "허브 간 이동 중인 배송만 목적지 허브에 도착할 수 있습니다."
            );
        }

        this.status = DeliveryStatus.ARRIVED_AT_DESTINATION_HUB;
    }


    private void validateCompanyManagerAssignmentStatus() {
        if (status != DeliveryStatus.ARRIVED_AT_DESTINATION_HUB) {
            throw new IllegalStateException(
                    "목적지 허브에 도착한 배송만 업체 배송담당자를 배정할 수 있습니다."
            );
        }
    }


}
