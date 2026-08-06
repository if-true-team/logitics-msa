package com.iftrue.hub.global.security;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class CurrentUserProvider {

    // TODO: Gateway 인증 방식과 전달 헤더 확정되면 실제 유저 정보 조회 로직으로 교체
    private static final UUID TEMP_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final String TEMP_USER_ROLE = "MASTER";

    @PostConstruct
    void warnTempAuthMessage() {
        log.warn("[Hub] 인증 연동 전 임시 유저 정보를 사용 중입니다.");
    }

    public UUID getCurrentUserId() {
        return TEMP_USER_ID;
    }

    public String getCurrentUserRole() {
        return TEMP_USER_ROLE;
    }
}
