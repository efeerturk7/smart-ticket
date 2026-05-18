package com.efeerturk.smart_ticket.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@UtilityClass
public class PagerUtil {
    public Pageable toPageable(RestPageableRequest request){
        if (!request.getColumnName().isEmpty()){
            Sort sortBy=request.isAsc()?Sort.by(Sort.Direction.ASC, request.getColumnName()):Sort.by(Sort.Direction.DESC, request.getColumnName());
            return PageRequest.of(request.getPageNumber() ,request.getPageSize(),sortBy);
        }else {
            return PageRequest.of(request.getPageNumber(), request.getPageSize());
        }
    }
    public <T> RestPageableEntity<T> toPageableResponse(Page<?> page , List<T> content){
        RestPageableEntity<T>restPageableEntity=new RestPageableEntity<>();
        restPageableEntity.setContent(content);
        restPageableEntity.setPageSize(page.getPageable().getPageSize());
        restPageableEntity.setPageNumber(page.getPageable().getPageNumber());
        restPageableEntity.setTotalElements(page.getTotalElements());
        return restPageableEntity;
    }
}
