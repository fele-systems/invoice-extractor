package com.systems.fele.users.dto;

import com.systems.fele.users.entity.AppUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean admin;
    private boolean enabled;

    public static UserDto fromAppUser(AppUser appUser) {
        return new UserDto(appUser.getId(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getEmail(),
                appUser.isAdmin(),
                appUser.isEnabled());
    }
}
