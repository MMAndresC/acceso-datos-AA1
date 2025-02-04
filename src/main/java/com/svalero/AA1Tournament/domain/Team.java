package com.svalero.AA1Tournament.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    @NotNull(message = "Team name is a mandatory field")
    private String name;

    @Column(nullable = false, length = 50)
    @NotNull(message = "Team representative is a mandatory field")
    private String representative;

    @Column(length = 15)
    private String phone;

    @ColumnDefault("false")
    private boolean partner;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(length = 80)
    private String address;

    @Column
    @NotNull(message = "Team has to be registered in a region")
    @Min(value = 1)
    @Max(value = 5)
    private int region;

}
