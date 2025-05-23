package com.svalero.tournament.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String alias;

    @Column(length = 15)
    private String phone;

    @Column(name = "birth_date")
    @Past
    private LocalDate birthDate;

    @Column(length = 15)
    private String position;

    @Column(name = "main_roster")
    @ColumnDefault("true")
    private boolean mainRoster;

    @ManyToOne
    @JoinColumn(name = "team_id")
    //Evita serializacion infinita de JSON, al estar relacionadas team-player entra en bucle
    @JsonManagedReference(value = "team_players")
    @OnDelete(action = OnDeleteAction.SET_NULL) //Not delete when team is deleted, FK = null
    private Team team;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference(value = "statistics_player")
    private List<Statistic> statistics;

}
