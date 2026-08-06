package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubService {

    private final HubRepository hubRepository;

    @Transactional
    public HubResponseDto createHub(HubCreateRequestDto request) {

        if (hubRepository.existsByNameAndDeletedAtIsNull(request.getName())) {
            throw new BusinessException(ErrorCode.HUB_NAME_DUPLICATED);
        }

        Hub hub = Hub.create(
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        );

        Hub savedHub = hubRepository.save(hub);

        log.info("[Hub] 허브 생성 완료 id={}", savedHub.getId());

        return HubResponseDto.from(savedHub);
    }

    public HubResponseDto getHub(UUID hubId) {
        Hub hub = hubRepository.findByIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HUB_NOT_FOUND));

        log.info("[Hub] 허브 단건 조회 id={}", hubId);

        return HubResponseDto.from(hub);
    }
}
