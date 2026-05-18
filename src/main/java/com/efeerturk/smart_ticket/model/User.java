package com.efeerturk.smart_ticket.model;

import com.efeerturk.smart_ticket.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User extends BaseModel {

    private String username;
    private String email;
    private String password;
    private Role role;


}
