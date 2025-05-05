package com.svalero.tournament.utils;


import com.svalero.tournament.domain.Statistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CreateCsv {

    public static File exportStatisticsToCsv(List<Statistic> statistics, String filename) throws IOException {
        String header = "ID, MVP, Kills, Deaths, Assists, Player Name, Player Alias, Team Name, Match ID, Match Date, Match Type, Tournament Name, Prize\n";
        //Temp file
        File file = File.createTempFile(filename, ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            //Header csv
            writer.write(header);
            for (Statistic statistic : statistics) {
                writer.write(statistic.getId() + ","
                        + statistic.isMvp() + ","
                        + statistic.getKills() + ","
                        + statistic.getDeaths() + ","
                        + statistic.getAssists() + ","
                        + statistic.getPlayer().getName() + ","
                        + statistic.getPlayer().getAlias() + ","
                        + statistic.getPlayer().getTeam().getName() + ","
                        + statistic.getMatch().getId() + ","
                        + statistic.getMatch().getDate() + ","
                        + statistic.getMatch().getType() + ","
                        + statistic.getMatch().getTournament().getName() + ","
                        + statistic.getMatch().getTournament().getPrize() + "\n");
            }
            return file;

        } catch (IOException e) {
            throw new IOException("Error generating csv file");
        }
    }
}
