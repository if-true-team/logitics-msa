package com.iftrue.order.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O-001", "주문을 찾을 수 없습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "O-002", "입력값 검증에 실패했습니다."),
    INSUFFICIENT_ORDER_QUANTITY(HttpStatus.CONFLICT, "O-003", "주문 수량이 부족합니다."),
    INVALID_ORDER_STATUS(HttpStatus.CONFLICT, "O-004", "허용되지 않는 주문 상태입니다."),
    ORDER_MODIFICATION_NOT_ALLOWED(HttpStatus.CONFLICT, "O-005", "주문을 수정할 수 없습니다."),
    ORDER_CANCELLATION_NOT_ALLOWED(HttpStatus.CONFLICT, "O-006", "주문을 취소할 수 없습니다."),
    DUPLICATE_ORDER_REQUEST(HttpStatus.CONFLICT, "O-007", "중복된 주문 요청입니다."),
    EXTERNAL_SERVICE_CALL_FAILED(HttpStatus.BAD_GATEWAY, "O-008", "외부 서비스 호출에 실패했습니다."),
    UNAUTHENTICATED_REQUEST(HttpStatus.UNAUTHORIZED, "O-009", "인증되지 않은 요청입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "O-010", "요청에 대한 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "O-011", "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
