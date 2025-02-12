package com.svalero.AA1Tournament.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "match")
@Table(name = "match_t")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime hour;

    @Column(nullable = false, length = 15)
    private String type;

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
    @OnDelete(action = OnDeleteAction.SET_NULL) //Not delete when caster is deleted, FK = null
    private Caster caster;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    @JsonManagedReference(value = "tournament_matches")
    private Tournament tournament;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference(value =  "match_details")
    private List<DetailsMatch> detailsMatches;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference(value = "statistics_match")
    private List<Statistic> statistics;

}
