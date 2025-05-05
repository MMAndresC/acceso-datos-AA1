package com.svalero.tournament.domain.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto {

    @NotNull(message = "Username required")
    private String username;

    @NotNull(message = "Password required")
    private String password;
}
