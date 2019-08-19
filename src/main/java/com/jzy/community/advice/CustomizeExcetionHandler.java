package com.jzy.community.advice;

import com.jzy.community.exception.CustmoizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jzy
 * @create 2019-08-14-9:43
 */
@ControllerAdvice
public class CustomizeExcetionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(HttpServletRequest request, Throwable e, Model model){

        model.addAttribute("message","可能访问了错误页面");

        return new ModelAndView("error");
    }
}
