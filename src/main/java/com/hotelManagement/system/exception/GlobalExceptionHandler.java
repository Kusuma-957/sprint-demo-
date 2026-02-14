package com.hotelManagement.system.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- SPECIFIC DOMAIN EXCEPTIONS ---

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .code(ApiCode.ADDFAILS) // as per CSV for add conflicts
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        ApiCode code = ApiCode.GETFAILS; // default
        // remap for certain URIs
        if (req.getRequestURI().contains("/update/")) {
            code = ApiCode.UPDTFAILS;
        } else if (req.getMethod().equalsIgnoreCase("DELETE")) {
            code = ApiCode.DLTFAILS;
        }
        ApiError body = ApiError.builder()
                .code(code)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(EmptyListException.class)
    public ResponseEntity<ApiError> handleEmptyList(EmptyListException ex, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .code(ApiCode.GETALLFAILS)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .code(ApiCode.BADREQUEST)
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- VALIDATION ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ApiError.FieldError.builder()
                        .field(fe.getField())
                        .error(resolveMessage(fe))
                        .rejectedValue(fe.getRejectedValue())
                        .build())
                .collect(toList());

        ApiError body = ApiError.builder()
                .code(ApiCode.VALIDATION_FAILS)
                .message("Validation failed")
                .errors(fieldErrors)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private String resolveMessage(FieldError fe) {
        return fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value";
    }

    // --- FALLBACKS ---

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ApiError> handleTx(TransactionSystemException ex, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .code(ApiCode.INTERNALERROR)
                .message("Data integrity/transaction error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        ApiError body = ApiError.builder()
                .code(ApiCode.INTERNALERROR)
                .message("Something went wrong")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}