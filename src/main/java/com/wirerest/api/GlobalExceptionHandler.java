package com.wirerest.api;


import com.wirerest.network.AlreadyUsedException;
import com.wirerest.shell.CommandExecutionException;
import com.wirerest.wireguard.ParsingException;
import com.wirerest.wireguard.peer.PeerAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;

import java.util.List;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<AppError> catchResourceNotFoundException(ResourceNotFoundException e) {
        return getAppErrorResponseEntity(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchException(Exception e) {
        logger.error("Unhandled exception", e);
        return getAppErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchNoSuchElementException(NoSuchElementException e) {
        return getAppErrorResponseEntity(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchIllegalArgumentException(IllegalArgumentException e) {
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchResponseStatusException(ResponseStatusException e) {
        return getAppErrorResponseEntity(e.getStatusCode(), e);
    }


    @ExceptionHandler
    public ResponseEntity<AppError> badRequestException(BadRequestException e) {
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> peerAlreadyExistsException(PeerAlreadyExistsException e) {
        return getAppErrorResponseEntity(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> serverWebInputException(ServerWebInputException e) {
        return getAppErrorResponseEntity(e.getStatusCode(), e.getCause());
    }

    @ExceptionHandler
    public ResponseEntity<AppError> TypeMismatchException(TypeMismatchException e) {
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> webExchangeBindException(WebExchangeBindException e) {
        List<String> errors = getValidationErrorStrings(e);
        return getAppErrorResponseEntity(e.getStatusCode(), String.join(", ", errors));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> constraintViolationException(ConstraintViolationException e) {
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    private List<String> getValidationErrorStrings(WebExchangeBindException e) {
        return e.getFieldErrors().stream()
                .map(fieldError -> "%s: %s (%s provided)".formatted(fieldError.getField(), fieldError.getDefaultMessage(), fieldError.getRejectedValue()))
                .toList();
    }

    private ResponseEntity<AppError> getAppErrorResponseEntity(HttpStatusCode statusCode, Throwable cause) {
        return new ResponseEntity<>(
                new AppError(statusCode.value(),
                        cause.getMessage()), statusCode);
    }

    private ResponseEntity<AppError> getAppErrorResponseEntity(HttpStatusCode statusCode, String causeMessage) {
        return new ResponseEntity<>(
                new AppError(statusCode.value(),
                        causeMessage), statusCode);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> alreadyUsed(AlreadyUsedException e) {
        return getAppErrorResponseEntity(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> commandExecutionException(CommandExecutionException e) {
        return getAppErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> parsingException(ParsingException e) {
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }
}
