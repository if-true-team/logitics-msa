package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubRouteCreateRequestDto;
import com.iftrue.hub.application.dto.HubRouteResponseDto;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubRouteService {

    private static final String ROLE_MASTER = "MASTER";

    private final HubRouteRepository hubRouteRepository;
    private final HubRepository hubRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public HubRouteResponseDto createHubRoute(HubRouteCreateRequestDto request) {
        checkMasterRole();

        UUID departureHubId = request.getDepartureHubId();
        UUID arrivalHubId = request.getArrivalHubId();

        if (departureHubId.equals(arrivalHubId)) {
            throw new BusinessException(ErrorCode.HUB_ROUTE_SAME_ENDPOINT);
        }

        validateHubExists(departureHubId);
        validateHubExists(arrivalHubId);

        if (hubRouteRepository.existsByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
                departureHubId, arrivalHubId)) {
            throw new BusinessException(ErrorCode.HUB_ROUTE_DUPLICATED);
        }

        HubRoute hubRoute = HubRoute.create(
                departureHubId,
                arrivalHubId,
                request.getDurationMinutes(),
                request.getDistanceKm()
        );

        HubRoute savedHubRoute = hubRouteRepository.save(hubRoute);

        log.info("[HubRoute] 허브 이동 경로 생성 완료 id={}", savedHubRoute.getId());

        return HubRouteResponseDto.from(savedHubRoute);
    }

    private void validateHubExists(UUID hubId) {
        if (!hubRepository.existsByIdAndDeletedAtIsNull(hubId)) {
            throw new BusinessException(ErrorCode.HUB_NOT_FOUND);
        }
    }

    private void checkMasterRole() {
        if (!ROLE_MASTER.equals(currentUserProvider.getCurrentUserRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
