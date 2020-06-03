package org.clickandcollect.webservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.clickandcollect.business.exception.ResourceDuplicationException;
import org.clickandcollect.business.exception.UnknownResourceException;
import org.clickandcollect.webservice.dto.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    public static final String LOGMSG = "Catching {} for {}";

    @ExceptionHandler({UnknownResourceException.class})
    public ResponseEntity<Object> unknownResource(Exception ex, WebRequest request) {
        return buildError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ResourceDuplicationException.class})
    public ResponseEntity<Object> uniqueConstraintException(Exception ex) {
        return buildError(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> validationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn(LOGMSG, ex.getClass(), ex.getMessage());
        Map<String, String> mapErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            mapErrors.put(error.getField(),error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            mapErrors.put(error.getObjectName(), error.getDefaultMessage());
        }

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getLocalizedMessage())
                .errors(mapErrors)
                .build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> badlyFormattedArgument(MethodArgumentTypeMismatchException ex, WebRequest request) {
        log.warn(LOGMSG, ex.getClass(), ex.getMessage());
        Map<String, String> mapErrors = new HashMap<>();
        mapErrors.put(ex.getName(),ex.getMessage());
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(ex.getLocalizedMessage())
                .errors(mapErrors)
                .build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @ResponseBody
    private ResponseEntity<Object> buildError(Exception ex, HttpStatus notFound) {
        log.warn(LOGMSG, ex.getClass(), ex.getMessage());
        ApiError apiError = ApiError.builder()
                .status(notFound)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}