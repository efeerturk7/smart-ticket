package com.efeerturk.smart_ticket.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class RestRootEntity<T>{
    private Integer status;
    private T payload;
    private String message;
    public static<T>RestRootEntity<T>ok(T payload){
        RestRootEntity<T>rootEntity=new RestRootEntity<>();
        rootEntity.setPayload(payload);
        rootEntity.setStatus(HttpStatus.OK.value());
        rootEntity.setMessage(null);
        return rootEntity;
    }
}
