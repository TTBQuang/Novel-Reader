package com.example.backend.exception;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Collections.singletonMap("message", message));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu.");
    }

    @ExceptionHandler(InvalidNovelStatusException.class)
    public ResponseEntity<Map<String, String>> handleInvalidNovelStatusException(InvalidNovelStatusException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Tham số truyền vào không đúng định dạng.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Thiếu tham số bắt buộc. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.CONFLICT, "Dữ liệu không hợp lệ hoặc bị trùng lặp.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Dữ liệu không đúng định dạng. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, String>> handleNumberFormatException(NumberFormatException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, "Dữ liệu số không đúng định dạng. Vui lòng nhập lại.");
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<Map<String, String>> handleUserRegistrationException(UserRegistrationException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Sai mật khẩu hoặc thông tin đăng nhập.");
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.UNAUTHORIZED, "Phiên đăng nhập không hợp lệ. Vui lòng thử lại");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.FORBIDDEN, "Không có quyền truy cập.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        System.out.println(ex.toString());
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<Map<String, String>> handleInvalidOtpException(InvalidOtpException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PasswordResetException.class)
    public ResponseEntity<Map<String, String>> handlePasswordResetException(PasswordResetException ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        System.out.println(ex.toString());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau");
    }
}