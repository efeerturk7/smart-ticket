package com.efeerturk.smart_ticket.utils;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class RestPageableRequest {

    @NotNull
    private int pageNumber;
    @NotNull
    private int pageSize;
    @NotBlank
    private String columnName;
    private boolean asc;
}
