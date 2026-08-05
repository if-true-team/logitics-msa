package com.iftrue.delivery.domain.deliveryroute;

import com.iftrue.delivery.domain.common.DeletableEntity;
import com.iftrue.delivery.domain.delivery.Delivery;
import com.iftrue.delivery.domain.deliverymanager.DeliveryManager;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "p_delivery_route",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_delivery_route_sequence",
                        columnNames = {"delivery_id", "sequence"}
                )
        }
)
public class DeliveryRoute extends DeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @Column(name = "hub_delivery_manager_id")
    private UUID hubDeliveryManagerId;

    @Column(name = "departure_hub_id", nullable = false)
    private UUID departureHubId;

    @Column(name = "arrival_hub_id", nullable = false)
    private UUID arrivalHubId;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private HubDeliveryStatus status;

    @Column(name = "departed_at")
    private Instant departedAt;

    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Column(name = "expected_distance", nullable = false, precision = 10, scale = 2)
    private BigDecimal expectedDistance;

    @Column(name = "expected_duration", nullable = false)
    private Integer expectedDuration;

    @Column(name = "actual_distance", precision = 10, scale = 2)
    private BigDecimal actualDistance;

    @Column(name = "actual_duration")
    private Integer actualDuration;

    private DeliveryRoute(
            Delivery delivery,
            UUID departureHubId,
            UUID arrivalHubId,
            int sequence,
            HubDeliveryStatus status

    ) {
        this.delivery = delivery;
        this.departureHubId = departureHubId;
        this.arrivalHubId = arrivalHubId;
        this.sequence = sequence;
        this.status = status;

    }

    public static DeliveryRoute create(
            Delivery delivery,
            UUID departureHubId,
            UUID arrivalHubId,
            int sequence,
            BigDecimal expectedDistance,
            Integer expectedDuration
    ) {
        return new DeliveryRoute(
                delivery,
                departureHubId,
                arrivalHubId,
                sequence,
                HubDeliveryStatus.WAITING_FOR_DEPARTURE
        );
    }


    public void assignHubDeliveryManager(DeliveryManager manager) {
        Objects.requireNonNull(manager, "배송담당자는 필수입니다.");

        if (status != HubDeliveryStatus.WAITING_FOR_DEPARTURE) {
            throw new IllegalStateException("출발 대기 상태의 경로에만 담당자를 배정할 수 있습니다.");
        }

        manager.validateHubDeliveryAssignable();

        this.hubDeliveryManagerId = manager.getId();
    }

    public void depart(Instant departedAt) {
        if (status != HubDeliveryStatus.WAITING_FOR_DEPARTURE) {
            throw new IllegalStateException("출발 대기 상태의 배송 경로만 출발할 수 있습니다.");
        }

        if (hubDeliveryManagerId == null) {
            throw new IllegalStateException("허브 배송담당자가 배정되지 않았습니다.");
        }

        this.departedAt = Objects.requireNonNull(departedAt, "출발 시각은 필수입니다.");
        this.status = HubDeliveryStatus.IN_TRANSIT;

    }

    public void arrive(
            Instant arrivedAt,
            BigDecimal actualDistance,
            Integer actualDuration
    ) {
        if (status != HubDeliveryStatus.IN_TRANSIT) {
            throw new IllegalStateException("이동 중인 배송 경로만 도착 처리할 수 있습니다.");
        }

        Instant validatedArrivedAt = Objects.requireNonNull(arrivedAt, "도착 시각은 필수입니다.");

        if (departedAt == null) {
            throw new IllegalStateException("출발 시각이 기록되지 않았습니다.");
        }

        if (validatedArrivedAt.isBefore(departedAt)) {
            throw new IllegalArgumentException("도착 시각은 출발 시각보다 빠를 수 없습니다.");
        }

        validateActualDistance(actualDistance);
        validateActualDuration(actualDuration);

        this.arrivedAt = validatedArrivedAt;
        this.actualDistance = actualDistance;
        this.actualDuration = actualDuration;
        this.status = HubDeliveryStatus.ARRIVED;
    }

    public boolean isArrived() {
        return status == HubDeliveryStatus.ARRIVED;
    }

    public boolean hasId(UUID routeId) {
        return Objects.equals(this.id, routeId);
    }

    private static void validateActualDistance(BigDecimal actualDistance) {
        if (actualDistance == null || actualDistance.signum() < 0) {
            throw new IllegalArgumentException("실제 거리는 0 이상이어야 합니다.");
        }
    }

    private static void validateActualDuration(Integer actualDuration) {
        if (actualDuration == null || actualDuration < 0) {
            throw new IllegalArgumentException("실제 소요 시간은 0 이상이어야 합니다.");
        }
    }

    public boolean hasSequence(int sequence) {
        return this.sequence == sequence;
    }


}
