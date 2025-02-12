package com.svalero.AA1Tournament.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "init_date",nullable = false)
    private LocalDate initDate;

    @Column(name="end_date", nullable = false)
    private LocalDate endDate;

    @Column
    @Min(0)
    private float prize;

    @Column(length = 80)
    private String address;

    @Column(length = 50)
    private String manager;

    @Column
    private float latitude;

    @Column
    private float longitude;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference(value = "tournament_matches")
    private List<Match> matches;
}
