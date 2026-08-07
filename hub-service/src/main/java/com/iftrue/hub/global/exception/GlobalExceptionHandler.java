package com.iftrue.hub.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {

        ErrorCode errorCode = exception.getErrorCode();

        log.warn("[Hub] 비즈니스 로직 에러: code={}, message={}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        Map<String, Object> errors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("[Hub] 입력값 검증 실패: {}", errors);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

        String requiredType = exception.getRequiredType() != null
                ? exception.getRequiredType().getSimpleName()
                : "올바른";

        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put(exception.getName(), requiredType + " 형식이어야 합니다.");

        log.warn("[Hub] 요청 인자 타입 불일치: param={}, value={}, requiredType={}", exception.getName(), exception.getValue(), requiredType);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, errors));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception) {

        ErrorCode errorCode = ErrorCode.HUB_NAME_DUPLICATED;

        // TODO: hub_route 추가 시 분기 수정 고려할 것
        log.warn("[Hub] 허브 이름 중복으로 데이터 무결성 제약 조건 위반: {}", exception.getMostSpecificCause().getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        log.error("[Hub] 처리되지 않은 예외 발생: ", exception);

        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of(errorCode));
    }
}
