package com.kakao.payment.advice;

import com.kakao.payment.common.dto.CommonResponse;
import com.kakao.payment.common.dto.ErrorResponse;
import com.kakao.payment.exception.BadRequestException;
import com.kakao.payment.exception.DuplicatedException;
import com.kakao.payment.exception.NotFoundException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CommonResponse> badRequestException(BadRequestException e) {
        log.error("BadRequestException - {}", e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(e.getMessage(), status.value());
        CommonResponse response = new CommonResponse(false, null, error);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<CommonResponse> duplicatedException(DuplicatedException e) {
        log.error("DuplicatedException - {}", e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(e.getMessage(), status.value());
        CommonResponse response = new CommonResponse(false, null, error);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CommonResponse> notFoundException(NotFoundException e) {
        log.error("NotFoundException - {}", e.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse error = new ErrorResponse(e.getMessage(), status.value());
        CommonResponse response = new CommonResponse(false, null, error);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> exception(Exception e) {
        log.error("Exception - {}", e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponse error = new ErrorResponse(e.getMessage(), status.value());
        CommonResponse response = new CommonResponse(false, null, error);
        return ResponseEntity.status(status).body(response);
    }
}
