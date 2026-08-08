package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubRouteCreateRequestDto;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.domain.HubRoute;
import com.iftrue.hub.domain.HubRouteRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.security.CurrentUserProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 허브 이동 경로 서비스 테스트")
class HubRouteServiceTest {

    @Mock
    private HubRouteRepository hubRouteRepository;

    @Mock
    private HubRepository hubRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private HubRouteService hubRouteService;

    @Test
    @DisplayName("출발 허브와 도착 허브가 같으면 H-005 예외가 발생하고 저장하지 않는다")
    void createRouteFailsWhenDepartureAndArrivalAreSame() {
        UUID hubId = UUID.randomUUID();
        HubRouteCreateRequestDto request = createRequest(hubId, hubId, 120, "325.50");
        given(currentUserProvider.getCurrentUserRole()).willReturn("MASTER");

        assertThatThrownBy(() -> hubRouteService.createHubRoute(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_ROUTE_SAME_ENDPOINT.getMessage());

        verify(hubRouteRepository, never()).save(any(HubRoute.class));
    }

    @Test
    @DisplayName("동일한 출발-도착 이동 경로가 이미 존재하면 H-004 예외가 발생하고 저장하지 않는다")
    void createRouteFailsWhenActiveRoutePairAlreadyExists() {
        UUID departureHubId = UUID.randomUUID();
        UUID arrivalHubId = UUID.randomUUID();
        HubRouteCreateRequestDto request = createRequest(departureHubId, arrivalHubId, 120, "325.50");
        given(currentUserProvider.getCurrentUserRole()).willReturn("MASTER");
        given(hubRepository.existsByIdAndDeletedAtIsNull(departureHubId)).willReturn(true);
        given(hubRepository.existsByIdAndDeletedAtIsNull(arrivalHubId)).willReturn(true);
        given(hubRouteRepository.existsByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(
                departureHubId, arrivalHubId)).willReturn(true);

        assertThatThrownBy(() -> hubRouteService.createHubRoute(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_ROUTE_DUPLICATED.getMessage());

        verify(hubRouteRepository, never()).save(any(HubRoute.class));
    }

    private HubRouteCreateRequestDto createRequest(
            UUID departureHubId, UUID arrivalHubId, Integer durationMinutes, String distanceKm) {
        HubRouteCreateRequestDto request = new HubRouteCreateRequestDto();
        ReflectionTestUtils.setField(request, "departureHubId", departureHubId);
        ReflectionTestUtils.setField(request, "arrivalHubId", arrivalHubId);
        ReflectionTestUtils.setField(request, "durationMinutes", durationMinutes);
        ReflectionTestUtils.setField(request, "distanceKm", new BigDecimal(distanceKm));
        return request;
    }
}
