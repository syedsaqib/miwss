package com.miw.gildedrose.api;

import com.miw.gildedrose.exception.ApiObjectNotFoundException;
import com.miw.gildedrose.exception.GildedRoseException;
import com.miw.gildedrose.exception.GrBadRequestException;
import com.miw.gildedrose.exception.GrErrorCode;
import com.miw.gildedrose.exception.GrUnAuthorizedException;
import com.miw.gildedrose.model.ErrorDto;
import com.miw.gildedrose.model.response.GrResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Api exception handler that translates to {@link ErrorDto} here
 *
 * @author ssaqib
 * @since v0.1
 */
@ControllerAdvice
@Slf4j
public class ApiErrorHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(ApiObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public GrResponse<ErrorDto> handleApiObjectNotFoundException(ApiObjectNotFoundException notFoundException) {
        ErrorDto errorDto = new ErrorDto(notFoundException.getErrorCode());
        log.error("Error -> {}", errorDto);

        return GrResponse.failure(errorDto);
    }

    /**
     * our app un-authorized on all generic spring AuthenticationException handler
     */
    @ExceptionHandler({GrUnAuthorizedException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public GrResponse<ErrorDto> handleUnAuthorizedException(RuntimeException rte) {
        ErrorDto errorDto = new ErrorDto(GrErrorCode.INVALID_LOGIN);
        log.error("Error -> {}", errorDto);

        if (rte instanceof AuthenticationException) {
            log.debug("---> internal exception: ", rte);
        }

        return GrResponse.failure(errorDto);
    }

    /**
     * our app access denied (403 forbidden) on all spring's access denied
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public GrResponse<ErrorDto> handleForbiddenException(AccessDeniedException ade) {
        ErrorDto errorDto = new ErrorDto(GrErrorCode.ACCESS_DENIED);
        log.error("Error -> {}", errorDto);
        log.debug("---> internal exception: ", ade);

        return GrResponse.failure(errorDto);
    }

    @ExceptionHandler(GrBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public GrResponse<ErrorDto> handleBadRequestException(GrBadRequestException badRequestException) {
        ErrorDto errorDto = new ErrorDto(badRequestException.getErrorCode());
        log.error("Error -> {}", errorDto);

        return GrResponse.failure(errorDto);
    }

    @ExceptionHandler(GildedRoseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public GrResponse<ErrorDto> handleGeneralApiException(GildedRoseException apiException) {
        ErrorDto errorDto = new ErrorDto(apiException.getErrorCode());
        log.error("Error -> {}", errorDto);

        return GrResponse.failure(errorDto);
    }


}
