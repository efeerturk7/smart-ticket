package com.efeerturk.smart_ticket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageType {
    USER_NOT_FOUND("1340","User not found"),
    TICKET_NOT_FOUND("1341","Ticket not found"),
    COMMENT_NOT_ADD("1435","Comment not added"),
    USER_ALREADY_EXISTS("1436","User already exists"),
    SYSTEM_ERROR("1342","System error"),
    TICKET_ALREADY_ASSIGNED("1343","Ticket already assigned"),
    TOO_MANY_REQUESTS("1344","Too many requests");
    private String code;
    private String message;
}
