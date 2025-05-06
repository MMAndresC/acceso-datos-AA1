package com.svalero.tournament.domain.dto.caster;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CasterInDto {
    @NotNull(message = "Caster name is a mandatory field")
    private String name;

    @NotNull(message = "Caster alias is a mandatory field")
    private String alias;

    private String phone;

    @Min(1)
    @Max(5)
    @NotNull(message = "Caster region required")
    private int region;

    private String languages;

    private LocalDate hireDate;
}
