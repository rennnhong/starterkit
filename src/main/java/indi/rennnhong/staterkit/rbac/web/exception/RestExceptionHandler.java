//package indi.rennnhong.staterkit.rbac.web.exception;
//
//import indi.rennnhong.staterkit.common.exception.ExceptionFactory;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
//public class RestExceptionHandler extends ResponseEntityExceptionHandler {
//
//    @ExceptionHandler(ExceptionFactory.ExistRelatedException.class)
//    protected ResponseEntity handleExistRelated(ExceptionFactory.ExistRelatedException e) {
//        return new ResponseEntity(
//                ResponseBody.newErrorMessageBody(ErrorMessages.DATA_EXIST_RELATED, e.getMessage()),
//                HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(ExceptionFactory.EntityNotFoundException.class)
//    protected ResponseEntity handleEntityNotFound(ExceptionFactory.EntityNotFoundException e) {
//        return new ResponseEntity(
//                ResponseBody.newErrorMessageBody(ErrorMessages.RESOURCE_NOT_FOUND, e.getMessage()),
//                HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(ExceptionFactory.DuplicateEntityException.class)
//    protected ResponseEntity handleDuplicateEntity(ExceptionFactory.DuplicateEntityException e) {
//        return new ResponseEntity(
//                ResponseBody.newErrorMessageBody(ErrorMessages.REQUEST_DUPLICATE_DATA, e.getMessage()),
//                HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(Exception.class)
//    protected ResponseEntity handleServerException(Exception exception,
//                                                           HttpServletRequest request, HttpServletResponse response) {
//        return new ResponseEntity(ErrorMessages.INTERNAL_SERVER_ERROR.toObject(),
//                HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//
//    @Override
//    protected ResponseEntity handleHttpMessageNotReadable(
//            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status,
//            WebRequest request) {
//        logger.debug(ex.getMessage());
//        return new ResponseEntity(ErrorMessages.INVALID_FIELDS_REQUEST.toObject(), headers,
//                status);
//    }
//
//}
