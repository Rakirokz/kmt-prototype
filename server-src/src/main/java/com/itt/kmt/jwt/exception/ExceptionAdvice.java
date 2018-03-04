
package com.itt.kmt.jwt.exception;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.itt.kmt.response.models.ResponseMsg;
import com.itt.utility.Constants;

/**
 * The Class ExceptionAdvice.
 */
@RestControllerAdvice
public class ExceptionAdvice {

    /**
     * Handle 401.
     *
     * @param e the e
     * @return the failure response msg
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public ModelMap handle401(final ShiroException e) {

        return new ModelMap().addAttribute(
            "success", new ResponseMsg(Boolean.FALSE, Constants.UNAUTHORIZED_ACCESS_MSG));
    }

    /**
     * Handle 401.
     *
     * @return the failure response msg
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public ModelMap handle401() {

        return new ModelMap().addAttribute(
            "success", new ResponseMsg(Boolean.FALSE, Constants.UNAUTHORIZED_ACCESS_MSG));
    }

    /**
     * Global exception.
     *
     * @param request the request
     * @param ex the ex
     * @return the failure response msg
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelMap globalException(final HttpServletRequest request, final Throwable ex) {
<<<<<<< c6d2e8d21cbcb263e7ec37ce92546dacb4d8ca5c
        ex.printStackTrace();
=======
>>>>>>> Refactoring and adding article type in response
        return new ModelMap().addAttribute("success", new ResponseMsg(Boolean.FALSE, Constants.BAD_REQUEST_MSG));
    }
}
