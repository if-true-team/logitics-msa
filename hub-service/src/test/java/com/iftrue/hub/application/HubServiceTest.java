package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.response.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 허브 서비스 단위 테스트")
public class HubServiceTest {

    @Mock
    private HubRepository hubRepository;

    @InjectMocks
    private HubService hubService;

    @DisplayName("허브를 생성하면 이름 중복을 검증하고 저장한다")
    @Test
    void createHub() {
        UUID hubId = UUID.randomUUID();
        HubCreateRequestDto request = hubCreateRequest("서울특별시 센터");

        given(hubRepository.existsByNameAndDeletedAtIsNull("서울특별시 센터")).willReturn(false);
        given(hubRepository.save(any(Hub.class)))
                .willAnswer(invocation -> {
                    Hub hub = invocation.getArgument(0);
                    ReflectionTestUtils.setField(hub, "id", hubId);
                    return hub;
                });

        HubResponseDto response = hubService.createHub(request);

        assertThat(response.id()).isEqualTo(hubId);
        assertThat(response.name()).isEqualTo("서울특별시 센터");
        assertThat(response.address()).isEqualTo("서울특별시 송파구 송파대로 55");
        assertThat(response.latitude()).isEqualByComparingTo("10.555555");
        assertThat(response.longitude()).isEqualByComparingTo("120.333333");
        verify(hubRepository).save(any(Hub.class));
    }

    @Test
    @DisplayName("허브가 이미 존재하면 허브 생성에 실패한다")
    void createHubDuplicate() {
        HubCreateRequestDto request = hubCreateRequest("서울특별시 센터");
        given(hubRepository.existsByNameAndDeletedAtIsNull("서울특별시 센터")).willReturn(true);

        assertThatThrownBy(() -> hubService.createHub(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.HUB_NAME_DUPLICATED.getMessage());

        verify(hubRepository, never()).save(any(Hub.class));
    }

    @Test
    @DisplayName("허브 목록을 조회하면 PageResponse로 변환해 반환한다")
    void getHubs() {
        Hub hub1 = hubWithId("서울특별시 센터", UUID.randomUUID());
        Hub hub2 = hubWithId("부산광역시 센터", UUID.randomUUID());
        Pageable pageable = PageRequest.of(0, 10);
        given(hubRepository.findAllByDeletedAtIsNull(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(hub1, hub2), pageable, 2));

        PageResponse<HubResponseDto> response = hubService.getHubs(pageable);

        assertThat(response.content())
                .extracting(HubResponseDto::name)
                .containsExactly("서울특별시 센터", "부산광역시 센터");
        assertThat(response.pageInfo().paginationType()).isEqualTo("OFFSET");
        assertThat(response.pageInfo().page()).isZero();
        assertThat(response.pageInfo().size()).isEqualTo(10);
        assertThat(response.pageInfo().totalElements()).isEqualTo(2);
        assertThat(response.pageInfo().totalPages()).isEqualTo(1);
    }

    private HubCreateRequestDto hubCreateRequest(String name) {
        HubCreateRequestDto request = new HubCreateRequestDto();
        ReflectionTestUtils.setField(request, "name", name);
        ReflectionTestUtils.setField(request, "address", "서울특별시 송파구 송파대로 55");
        ReflectionTestUtils.setField(request, "latitude", new BigDecimal("10.555555"));
        ReflectionTestUtils.setField(request, "longitude", new BigDecimal("120.333333"));
        return request;
    }

    private Hub hubWithId(String name, UUID id) {
        Hub hub = Hub.create(name, "테스트 주소",
                new BigDecimal("37.123456"), new BigDecimal("126.123456"));
        ReflectionTestUtils.setField(hub, "id", id);
        return hub;
    }
}
