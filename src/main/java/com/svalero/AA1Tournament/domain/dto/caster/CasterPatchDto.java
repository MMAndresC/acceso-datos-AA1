package com.svalero.AA1Tournament.domain.dto.caster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CasterPatchDto {

    private String alias;

    private String phone;

    @Min(1)
    @Max(5)
    private Integer region;

    private String languages;
}
