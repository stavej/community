package com.jzy.community.advice;

import com.alibaba.fastjson.JSON;
import com.jzy.community.dto.ResultDTO;
import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author jzy
 * @create 2019-08-14-9:43
 */
@ControllerAdvice
public class CustomizeExcetionHandler {
    @ExceptionHandler(Exception.class)
    ModelAndView handle(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response){
        String contenType =  request.getContentType();

        if ("application/json".equals(contenType)){
            ResultDTO resultDTO;
            //返回JSON
            if (e instanceof CustmoizeException){
                resultDTO =  ResultDTO.errorOf((CustmoizeException) e);
            }else {
                resultDTO =  ResultDTO.errorOf(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("utf-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            }catch (Exception ioe){

            }
            return null;
        }else {
            //错误页面跳转
            if (e instanceof CustmoizeException){
                model.addAttribute("message",e.getMessage());
            }else {
                model.addAttribute("message",CustomizeErrorCode.SYS_ERROR.getMassage());
            }
            return new ModelAndView("error");
        }


    }
}
