package com.iftrue.delivery.domain.deliverymanager;

import com.iftrue.delivery.domain.common.DeletableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_delivery_manager")
public class DeliveryManager extends DeletableEntity {

    @Id
    private UUID id;

    @Column(name = "slack_id", nullable = false, length = 50)
    private String slack_id;

    @Column(name = "hub_id")
    private UUID hubId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private DeliveryManagerType type;

    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    public void validateCompanyDeliveryAssignable(UUID destinationHubId) {
        if (type != DeliveryManagerType.COMPANY) {
            throw new IllegalStateException(
                    "업체 배송담당자만 업체 배송을 담당할 수 있습니다."
            );
        }

        if (isDeleted()) {
            throw new IllegalStateException(
                    "삭제된 배송담당자는 배정할 수 없습니다."
            );
        }

        if (!Objects.equals(hubId, destinationHubId)) {
            throw new IllegalStateException(
                    "목적지 허브 소속 배송담당자가 아닙니다."
            );
        }
    }
}
