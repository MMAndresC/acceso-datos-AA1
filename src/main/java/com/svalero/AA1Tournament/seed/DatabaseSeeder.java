package com.svalero.AA1Tournament.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {
    private final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    @Bean
    public CommandLineRunner seedDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            String fields = "";
            String values = "";
            // Only when tables are empty
            //TEAM
            int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM team", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding team table...");
                fields = "INSERT INTO team (name, representative, phone, partner, registration_date, address, region) VALUES ";
                values = "('Virtus Pro', 'Aapo Vartiainen', '654123987', true, '2023-05-14', 'Armenia', 3)";
                jdbcTemplate.execute(fields  + values);
                values = "('Gen.G', 'Kent Wakeford', '678456123', true, '2023-05-05', 'South Korea', 3)" ;
                jdbcTemplate.execute(fields + values);

                values = "('Sakura', 'Kent Wakeford', '678456123', false, '2023-05-05', 'France', 3)" ;
                jdbcTemplate.execute(fields + values);

                this.logger.info("Seeding completed");
            } else {
                this.logger.info("team table already seeded. Skipping...");
            }

            //CASTER
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM caster", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding caster table...");
                fields = "INSERT INTO caster (name, alias, phone, region, hire_date, languages) VALUES ";
                values = "('Matt Morello', 'Mr X', '623145698', 3, '2023-06-02', 'english')";
                jdbcTemplate.execute(fields  + values);
                values = "('Harry Pollit', 'LEGDAY', '623145987', 3, '2023-06-27', 'english, spanish')";
                jdbcTemplate.execute(fields + values);

                values = "('TO DELETE', 'LEGDAY', '623145987', 3, '2023-06-27', 'finnish')";
                jdbcTemplate.execute(fields + values);

                this.logger.info("Seeding completed");
            } else {
                this.logger.info("caster table already seeded. Skipping...");
            }

            //TOURNAMENT
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tournament", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding tournament table...");
                fields = "INSERT INTO tournament (name, init_date, end_date, prize, address, manager, latitude, longitude) VALUES ";
                values = "('Dragons community open tournament #9', '2025-03-26', '2025-03-31', 500.80, 'Paris, France', 'Clara Moreau', 48.85661400, 2.35222190)";
                jdbcTemplate.execute(fields  + values);
                values = "('FACEIT league Season 3 - EMEA masters', '2025-10-07', '2025-12-15', 17000, 'Berlin, Germany', 'Mark Salling', 52.52000660, 13.40495400)";
                jdbcTemplate.execute(fields + values);

                values = "('Prove your luck', '2024-10-07', '2024-12-15', 17000, 'Berlin, Germany', 'Mark Salling', 52.52018660, 13.40495500)";
                jdbcTemplate.execute(fields + values);

                this.logger.info("Seeding completed");
            } else {
                this.logger.info("tournament table already seeded. Skipping...");
            }

            //PLAYER
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM player", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding player table...");
                fields = "INSERT INTO player (name, alias, phone, birth_date, position, main_roster, team_id) VALUES ";
                values = "('Christian Ríos', 'Khenail', '654789321', '2005-08-20', 'support', true, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "('Paavo Ulmanen', 'Sauna', '647888956', '2004-10-03', 'DPS', true, 2)";
                jdbcTemplate.execute(fields + values);
                values = "('Kim Tae-sung', 'Mag', '622365894', '2002-06-12', 'tank', true, 2)";
                jdbcTemplate.execute(fields + values);
                values = "('Jesus Nuñez Lopez', 'Galaa', '688741235', '2004-04-09', 'support', true, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "('Niclas Smidt Jensen', 'sHockWave', '685125795', '2000-07-25', 'DPS', true, 1)";
                jdbcTemplate.execute(fields + values);
                values = "('Ilari Vestola', 'Vestola', '699854123', '2000-04-07', 'tank', true, 1)";
                jdbcTemplate.execute(fields + values);

                values = "('TO DELETE', 'Vestola', '699854123', '2000-04-07', 'tank', true, 3)";
                jdbcTemplate.execute(fields + values);

                this.logger.info("Seeding completed");
            } else {
                this.logger.info("player table already seeded. Skipping...");
            }

            //MATCH
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM match_t", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding match_t table...");
                fields = "INSERT INTO match_t (date, hour, type, map_name, duration, day, caster_id, tournament_id) VALUES ";
                values = "('2024-10-17', '18:00', 'group stage', 'Samoa', 21, 2, 2, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "('2024-12-15', '18:00', 'final', 'Watchpoint:Gibraltar', 28, 12, 1, 2)";
                jdbcTemplate.execute(fields + values);
                values = "('2025-01-30', '19:00', 'quarter-finals', 'Suravasa', 19, 5, 2, 1)";
                jdbcTemplate.execute(fields + values);

                //TO DELETE
                values = "('2025-01-30', '19:00', 'quarter-finals', 'TO DELETE', 19, 5, 3, 3)";
                jdbcTemplate.execute(fields + values);

                this.logger.info("Seeding completed");
            } else {
                this.logger.info("match_t table already seeded. Skipping...");
            }

            //DETAILS MATCH
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM detail_match_team", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding detail_match_team table...");
                fields = "INSERT INTO detail_match_team (score, winner, kills, deaths, assists, match_id, team_id) VALUES ";
                values = "(2, true, 75, 32, 49, 1, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(1, false, 68, 42, 33, 1, 2)";
                jdbcTemplate.execute(fields + values);

                values = "(5, true, 90, 43, 49, 2, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(4, false, 77, 51, 41, 2, 2)";
                jdbcTemplate.execute(fields + values);

                values = "(2, false, 74, 43, 51, 3, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(3, true, 86, 38, 52, 3, 2)";
                jdbcTemplate.execute(fields + values);

                //TO DELETE
               /* values = "(3, true, 86, 38, 52, 4, 3)";
                jdbcTemplate.execute(fields + values);*/

                this.logger.info("Seeding completed");
            } else {
                this.logger.info("detail_match_team table already seeded. Skipping...");
            }

            //STATITICS
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM statistics_tournament_player", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding statistics_tournament_player table...");
                fields = "INSERT INTO statistics_tournament_player (mvp, kills, deaths, assists, player_id, match_id) VALUES ";

                values = "(false, 15, 6, 21, 1, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 12, 8, 17, 1, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 13, 7, 20, 1, 3)";
                jdbcTemplate.execute(fields  + values);


                values = "(false, 25, 12, 7, 2, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(true, 30, 15, 12, 2, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 22, 13, 6, 2, 3)";
                jdbcTemplate.execute(fields  + values);


                values = "(false, 13, 10, 10, 3, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 21, 16, 20, 3, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 15, 6, 9, 3, 3)";
                jdbcTemplate.execute(fields  + values);

                values = "(true, 8, 5, 30, 4, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 20, 11, 41, 4, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 12, 7, 14, 4, 3)";
                jdbcTemplate.execute(fields  + values);

                values = "(false, 25, 12, 7, 5, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 30, 15, 12, 5, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 22, 13, 6, 5, 3)";
                jdbcTemplate.execute(fields  + values);

                values = "(false, 25, 12, 7, 6, 1)";
                jdbcTemplate.execute(fields  + values);
                values = "(false, 30, 15, 12, 6, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "(true, 22, 13, 6, 6, 3)";
                jdbcTemplate.execute(fields  + values);

                //TO DELETE
                values = "(false, 30, 15, 12, 7, 4)";
                jdbcTemplate.execute(fields  + values);
                this.logger.info("Seeding completed");
            } else {
                this.logger.info("statistics_tournament_player table already seeded. Skipping...");
            }
        };
    }
}
