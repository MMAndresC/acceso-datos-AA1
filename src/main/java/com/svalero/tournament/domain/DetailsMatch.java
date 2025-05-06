package com.svalero.tournament.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "detail_match_team")
public class DetailsMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @ColumnDefault("0")
    @Min(0)
    private int score;

    @Column
    @ColumnDefault("false")
    private boolean winner;

    @Column
    @ColumnDefault("0")
    @Min(0)
    private int kills;

    @Column
    @ColumnDefault("0")
    @Min(0)
    private int deaths;

    @Column
    @ColumnDefault("0")
    @Min(0)
    private int assists;

    @ManyToOne
    @JoinColumn(name = "match_id")
    @JsonManagedReference(value = "match_details")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonManagedReference(value = "team_details_matches")
    @OnDelete(action = OnDeleteAction.SET_NULL) //Not delete when team is deleted, FK = null
    private Team team;

}
