package com.example.ZhangDT.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessage<T> {
    private Integer code;

    private String message;

    private T data;

    public static <T> ResponseMessage<T> success(T data){
        return new ResponseMessage<T>(HttpStatus.OK.value(), "success", data);
    }

    public static <T> ResponseMessage<T> success(){
        return new ResponseMessage<T>(HttpStatus.OK.value(), "success", null);
    }

    public static <T> ResponseMessage<T> fail(String message) {
        return new ResponseMessage<T>(HttpStatus.OK.value(), message, null);
    }


}
