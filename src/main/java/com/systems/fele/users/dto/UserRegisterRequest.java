package com.systems.fele.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean admin;

}
