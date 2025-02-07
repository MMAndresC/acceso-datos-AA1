package com.svalero.AA1Tournament.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "statistics_tournament_player")
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @ColumnDefault("false")
    private boolean mvp;

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
    @JoinColumn(name = "match_id")
    @JsonManagedReference(value = "statistics_match")
    private Match match;

}
