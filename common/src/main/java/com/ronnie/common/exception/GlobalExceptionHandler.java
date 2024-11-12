package com.ronnie.common.exception;

import com.ronnie.common.api.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //    @ExceptionHandler(MissingServletRequestParameterException.class)
//    public ErrorResult handleError(MissingServletRequestParameterException e) {
//        logger.warn("Missing Request Parameter", e);
//        String message = String.format("Missing Request Parameter: %s", e.getParameterName());
//        return new ErrorResult(message);
//    }
//
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleError(MethodArgumentTypeMismatchException e) {
        logger.warn("Method Argument Type Mismatch", e);
        String message = String.format("Method Argument Type Mismatch: %s", e.getName());
        return Result.error(HttpStatus.BAD_REQUEST.value(), message);
    }

    //
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleError(MethodArgumentNotValidException e) {
        logger.warn("Method Argument Not Valid", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String message = String.format("%s:%s", error.getField(), error.getDefaultMessage());
        return Result.error(HttpStatus.BAD_REQUEST.value(), message);
    }
//
//    @ExceptionHandler(BindException.class)
//    public ErrorResult handleError(BindException e) {
//        logger.warn("Bind Exception", e);
//        FieldError error = e.getFieldError();
//        String message = String.format("%s:%s", error.getField(), error.getDefaultMessage());
//        return new ErrorResult(message);
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ErrorResult handleError(ConstraintViolationException e) {
//        logger.warn("Constraint Violation", e);
//        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
//        ConstraintViolation<?> violation = violations.iterator().next();
//        String path = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
//        String message = String.format("%s:%s", path, violation.getMessage());
//        return new ErrorResult(message);
//    }
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public ErrorResult handleError(HttpMessageNotReadableException e) {
//        logger.error("Message Not Readable", e);
//        return new ErrorResult(e.getMessage());
//    }
//
//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public ErrorResult handleError(HttpRequestMethodNotSupportedException e) {
//        logger.error("Request Method Not Supported", e);
//        return new ErrorResult(e.getMessage());
//    }
//
//    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
//    public ErrorResult handleError(HttpMediaTypeNotSupportedException e) {
//        logger.error("Media Type Not Supported", e);
//        return new ErrorResult(e.getMessage());
//    }
//
//    @ExceptionHandler(ServiceException.class)
//    public ErrorResult handleError(ServiceException e) {
//        logger.error("Service Exception", e);
//        return new ErrorResult(e.getMessage());
//    }

//    @ExceptionHandler(Throwable.class)
//    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResult<?> handleError(Throwable e) {
//        logger.error("Internal Server Error", e);
//        return new ErrorResult<>( UNKNOWN_ERROR,e.getMessage());
//    }
}
