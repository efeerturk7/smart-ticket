package com.efeerturk.smart_ticket.model;

import com.efeerturk.smart_ticket.enums.Priority;
import com.efeerturk.smart_ticket.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "tickets")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Ticket extends BaseModel {

    private String title;
    private String description;
    private Status status;
    private Priority priority;
    @ManyToOne
    private User user;


}
