package com.svalero.AA1Tournament.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "statistics_tournament_player")
public class StatisticsTournamentPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @ColumnDefault("false")
    private boolean mvp;

    @Column(name = "map_played", length = 20)
    @NotNull(message = "Name of map required")
    private String mapPlayed;

    @Column
    @Min(0)
    private int kills;

    @Column
    @Min(0)
    private int deaths;

    @Column
    @Min(0)
    private int assists;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonManagedReference(value = "statistics_player")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    @JsonManagedReference(value = "statistics_tournament")
    private Tournament tournament;

}
