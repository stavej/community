package com.jzy.community.dto;

import com.jzy.community.exception.CustmoizeException;
import com.jzy.community.exception.CustomizeErrorCode;
import lombok.Data;

/**
 * @author jzy
 * @create 2019-08-20-9:31
 */
@Data
public class ResultDTO {
    private Integer code;
    private String message;

    public static ResultDTO errorOf(Integer code,String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        return errorOf(errorCode.getCode(),errorCode.getMassage());
    }

    public static ResultDTO errorOf(CustmoizeException e) {
        return errorOf(e.getCode(),e.getMessage());
    }

    public static ResultDTO okOf(){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }


}
