package com.example.backend.exception;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy dữ liệu.");
    }

    @ExceptionHandler(InvalidNovelStatusException.class)
    public ResponseEntity<String> handleInvalidNovelStatusException(InvalidNovelStatusException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tham số truyền vào không đúng định dạng.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu tham số bắt buộc. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Dữ liệu không hợp lệ hoặc bị trùng lặp.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dữ liệu không đúng định dạng. Vui lòng kiểm tra lại.");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dữ liệu số không đúng định dạng. Vui lòng nhập lại.");
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<String> handleUserRegistrationException(UserRegistrationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai mật khẩu hoặc thông tin đăng nhập.");
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Phiên đăng nhập không hợp lệ. Vui lòng thử lại");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền truy cập.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        System.out.println(ex.toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau");
    }
}
