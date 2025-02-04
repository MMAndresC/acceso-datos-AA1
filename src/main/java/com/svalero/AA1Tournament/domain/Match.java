package com.svalero.AA1Tournament.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "match")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private LocalDate date;

    @Column
    private LocalTime hour;

    @Column(name = "map_name")
    private String mapName;

    @Column
    @Min(0)
    private int duration; //Minutos

    @Column
    @Min(1)
    private int day; //Nº de jornada

    @ManyToOne
    @JoinColumn(name = "caster_id")
    @JsonManagedReference(value = "caster_matches")
    private Caster caster;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    @JsonManagedReference(value = "tournament_matches")
    private Tournament tournament;
}
