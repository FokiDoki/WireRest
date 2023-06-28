package com.wireguard.api;


import com.wireguard.external.network.AlreadyUsedException;
import com.wireguard.external.network.NoFreeIpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler
    public ResponseEntity<AppError> catchResourceNotFoundException(ResourceNotFoundException e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> catchException(Exception e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler

    public ResponseEntity<AppError> badRequestException(BadRequestException e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<AppError> serverWebInputException(ServerWebInputException e) {
        logger.warn("ServerWebInputException: ".formatted(e.getCause().getMessage()));
        return new ResponseEntity<>(
                new AppError(e.getStatusCode().value(),
                        e.getCause().getMessage()), e.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<AppError> alreadyUsed(AlreadyUsedException e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new AppError(HttpStatus.BAD_REQUEST.value(),
                        e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
