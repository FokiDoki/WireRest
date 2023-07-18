package com.wireguard.api;


import com.wireguard.external.network.AlreadyUsedException;
import com.wireguard.external.shell.CommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

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
    public ResponseEntity<AppError> badRequestException(BadRequestException e) {
        logger.error(e.getMessage(), e);
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> serverWebInputException(ServerWebInputException e) {
        logger.warn("ServerWebInputException: %s".formatted(e.getCause().getMessage()));
        return getAppErrorResponseEntity(e.getStatusCode(), e.getCause());
    }

    @ExceptionHandler
    public ResponseEntity<AppError> webExchangeBindException(WebExchangeBindException e) {
        logger.warn("Input validation error: %s".formatted(e.getMessage()));
        return getAppErrorResponseEntity(HttpStatus.BAD_REQUEST, e);
    }

    private ResponseEntity<AppError> getAppErrorResponseEntity(HttpStatusCode statusCode, Throwable cause) {
        return new ResponseEntity<>(
                new AppError(statusCode.value(),
                        cause.getMessage()), statusCode);
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
}
