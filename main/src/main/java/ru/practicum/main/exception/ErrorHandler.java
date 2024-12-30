package ru.practicum.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final DataConflictException ex) {
        log.error("409 CONFLICT ", ex);
        return new ErrorResponse(HttpStatus.CONFLICT.toString(),
                "Request creates a data conflict in the database",
                ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final ElementNotFoundException ex) {
        log.error("404 NOT_FOUND {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.toString(),
                "Element not found in Database",
                ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException ex) {
        log.error("400 BAD_REQUEST {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Bad request",
                ex.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(final ForbiddenException ex) {
        log.error("403 FORBIDDEN {}", ex.getMessage());
        return new ErrorResponse(HttpStatus.FORBIDDEN.toString(),
                "Access denied",
                ex.getMessage());
    }

}
