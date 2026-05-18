package com.efeerturk.smart_ticket.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError<E> {
    private Integer status;

    private Exception<E> exception;
}
