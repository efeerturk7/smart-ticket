package com.efeerturk.smart_ticket.exception;

import com.efeerturk.smart_ticket.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private MessageType messageType;

    private String ofStatic;



    public String prepareErrorMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(messageType.getMessage());
        if(this.ofStatic!=null) {
            builder.append(" : " + ofStatic);
        }
        return builder.toString();
    }
}
