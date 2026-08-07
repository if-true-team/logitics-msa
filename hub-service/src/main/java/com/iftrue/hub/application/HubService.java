package com.iftrue.hub.application;

import com.iftrue.hub.application.dto.HubCreateRequestDto;
import com.iftrue.hub.application.dto.HubResponseDto;
import com.iftrue.hub.application.dto.HubUpdateRequestDto;
import com.iftrue.hub.domain.Hub;
import com.iftrue.hub.domain.HubRepository;
import com.iftrue.hub.global.exception.BusinessException;
import com.iftrue.hub.global.exception.ErrorCode;
import com.iftrue.hub.global.response.PageResponse;
import com.iftrue.hub.global.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HubService {

    private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 30, 50);
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final Set<String> ALLOWED_SORT = Set.of("createdAt", "updatedAt", "name");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    private final HubRepository hubRepository;

    private final CurrentUserProvider currentUserProvider;

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

    public PageResponse<HubResponseDto> getHubs(Pageable pageable) {
        Page<HubResponseDto> hubPage = hubRepository.findAllByDeletedAtIsNull(toHubPageable(pageable))
                .map(HubResponseDto::from);

        log.info("[Hub] 허브 목록 조회 page={} size={} totalElements={}",
                hubPage.getNumber(), hubPage.getSize(), hubPage.getTotalElements());

        return PageResponse.from(hubPage);
    }

    public PageResponse<HubResponseDto> searchHub(String keyword, Pageable pageable) {
        String searchKeyword = (keyword == null) ? "" : keyword;

        Page<HubResponseDto> hubPage = hubRepository.search(searchKeyword, toHubPageable(pageable))
                .map(HubResponseDto::from);

        log.info("[Hub] 허브 검색 keyword={} totalElements={}", keyword, hubPage.getTotalElements());

        return PageResponse.from(hubPage);
    }

    @Transactional
    public HubResponseDto updateHub(UUID hubId, HubUpdateRequestDto request) {

        Hub hub = hubRepository.findByIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HUB_NOT_FOUND));

        String newName = resolveNewName(hub, request.getName());

        hub.update(newName, request.getAddress(), request.getLatitude(), request.getLongitude());

        log.info("[Hub] 허브 정보 수정 완료 id={}", hubId);

        return HubResponseDto.from(hub);
    }

    @Transactional
    public void deleteHub(UUID hubId) {

        Hub hub = hubRepository.findByIdAndDeletedAtIsNull(hubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HUB_NOT_FOUND));

        hub.softDelete(currentUserProvider.getCurrentUserId());

        // TODO: HubRoute 구현 후, 해당 허브가 출발 또는 도착인 경로도 함께 soft delete

        log.info("[Hub] 허브 삭제 완료 id={}", hubId);
    }

    private String resolveNewName(Hub hub, String rawName) {
        if (rawName == null) {
            return null;
        }

        String normalized = rawName.trim();

        if (!normalized.equals(hub.getName())
                && hubRepository.existsByNameAndDeletedAtIsNull(normalized)) {
            throw new BusinessException(ErrorCode.HUB_NAME_DUPLICATED);
        }

        return normalized;
    }

    private Pageable toHubPageable(Pageable requestedPageable) {

        int pageSize = ALLOWED_SIZES.contains(requestedPageable.getPageSize())
                ? requestedPageable.getPageSize()
                : DEFAULT_PAGE_SIZE;

        List<Sort.Order> validSortOrders = requestedPageable.getSort()
                .stream()
                .filter(order -> ALLOWED_SORT.contains(order.getProperty()))
                .toList();

        Sort resolvedSort = validSortOrders.isEmpty()
                ? DEFAULT_SORT
                : Sort.by(validSortOrders);

        return PageRequest.of(
                requestedPageable.getPageNumber(),
                pageSize,
                resolvedSort
        );
    }
}
