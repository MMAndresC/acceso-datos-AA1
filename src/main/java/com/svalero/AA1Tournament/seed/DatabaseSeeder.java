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
                this.logger.info("Seeding completed");
            } else {
                this.logger.info("Team table already seeded. Skipping...");
            }

            //CASTER
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM caster", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding caster table...");
                fields = "INSERT INTO caster (name, alias, phone, region, hire_date) VALUES ";
                values = "('Matt Morello', 'Mr X', '623145698', 3, '2023-06-02')";
                jdbcTemplate.execute(fields  + values);
                values = "('Harry Pollit', 'LEGDAY', '623145987', 3, '2023-06-27')";
                jdbcTemplate.execute(fields + values);
                this.logger.info("Seeding completed");
            } else {
                this.logger.info("Caster table already seeded. Skipping...");
            }

            //TOURNAMENT
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tournament", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding tournament table...");
                fields = "INSERT INTO tournament (name, init_date, end_date, prize, address, manager, latitude, longitude) VALUES ";
                values = "('Dragons community open tournament #9', '2025-01-26', '2025-01-31', 500.80, 'Paris, France', 'Clara Moreau', 48.85661400, 2.35222190)";
                jdbcTemplate.execute(fields  + values);
                values = "('FACEIT league Season 3 - EMEA masters', '2024-10-7', '2024-12-15', 17000, 'Berlin, Germany', 'Mark Salling', 52.52000660, 13.40495400)";
                jdbcTemplate.execute(fields + values);
                this.logger.info("Seeding completed");
            } else {
                this.logger.info("Tournament table already seeded. Skipping...");
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
                this.logger.info("Seeding completed");
            } else {
                this.logger.info("Player table already seeded. Skipping...");
            }

            //MATCH
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM match_t", Integer.class);
            if (count == 0) {
                this.logger.info("Seeding match table...");
                fields = "INSERT INTO match_t (date, hour, type, map_name, duration, day, caster_id, tournament_id) VALUES ";
                values = "('2024-10-17', '18:00', 'group stage', 'Samoa', 21, 2, 2, 2)";
                jdbcTemplate.execute(fields  + values);
                values = "('2024-12-15', '18:00', 'final', 'Watchpoint:Gibraltar', 28, 12, 1, 2)";
                jdbcTemplate.execute(fields + values);
                values = "('2025-01-30', '19:00', 'quarter-finals', 'Suravasa', 19, 5, 2, 1)";
                jdbcTemplate.execute(fields + values);
                this.logger.info("Seeding completed");
            } else {
                this.logger.info("Match table already seeded. Skipping...");
            }
        };
    }
}
