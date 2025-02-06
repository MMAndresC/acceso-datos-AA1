package com.svalero.AA1Tournament.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.List;

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

    @Column
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

    @OneToMany(mappedBy = "team")
    //Tiene que coincidir con el valor @JsonManagedReference de la otra tabla relacionada
    @JsonBackReference(value = "team_players")
    private List<Player> player;

    @OneToMany(mappedBy = "team")
    @JsonBackReference(value = "team_details_matches")
    private List<DetailsMatch> detailsMatch;

}
