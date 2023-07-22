package com.wireguard.api;


import com.wireguard.external.network.AlreadyUsedException;
import com.wireguard.external.shell.CommandExecutionException;
import com.wireguard.external.wireguard.ParsingException;
import com.wireguard.external.wireguard.peer.PeerAlreadyExistsException;
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
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<AppError> catchResourceNotFoundException(ResourceNotFoundException e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchException(Exception e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchNoSuchElementException(NoSuchElementException e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchIllegalArgumentException(IllegalArgumentException e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchResponseStatusException(ResponseStatusException e) {
        logger.info(e.getMessage());
        return getAppErrorResponseEntity(e.getStatusCode(), e);
    }


    @ExceptionHandler
    public ResponseEntity<AppError> badRequestException(BadRequestException e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> peerAlreadyExistsException(PeerAlreadyExistsException e) {
        logger.info(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> serverWebInputException(ServerWebInputException e) {
        logger.warn("ServerWebInputException: %s".formatted(e.getCause().getMessage()));
        return getAppErrorResponseEntity(e.getStatusCode(), e.getCause());
    }

    @ExceptionHandler
    public ResponseEntity<AppError> TypeMismatchException(TypeMismatchException e) {
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> webExchangeBindException(WebExchangeBindException e) {
        logger.debug("Validation error: %s".formatted(e.getMessage()));
        List<String> errors = getValidationErrorStrings(e);
        return getAppErrorResponseEntity(e.getStatusCode(), String.join(", ", errors));
    }

    @ExceptionHandler
    public ResponseEntity<AppError> constraintViolationException(ConstraintViolationException e) {
        logger.debug("Validation error: %s".formatted(e.getMessage()));
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
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> commandExecutionException(CommandExecutionException e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> parsingException(ParsingException e){
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }
}
