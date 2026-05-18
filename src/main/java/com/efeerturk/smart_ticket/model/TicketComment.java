package com.efeerturk.smart_ticket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ticket_comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TicketComment extends BaseModel{
    @ManyToOne
    private Ticket ticket;
    @ManyToOne
    private User user;
    private String content;
}
