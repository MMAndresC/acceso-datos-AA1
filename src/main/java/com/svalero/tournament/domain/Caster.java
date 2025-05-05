package com.svalero.tournament.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "caster")
public class Caster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String alias;

    @Column(length = 15)
    private String phone;

    @Column
    @Min(1)
    @Max(5)
    @NotNull(message = "Region required")
    private int region;

    @Column(length = 100)
    private String languages;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @OneToMany(mappedBy = "caster")
    @JsonBackReference(value = "caster_matches")
    private List<Match> matches;

}
