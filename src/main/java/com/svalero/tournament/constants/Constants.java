package com.svalero.tournament.constants;

import java.util.Map;

public class Constants {

    public static final Map<Integer, String> REGIONS = Map.of(
            1, "North America",
            2, "South America",
            3, "Europe",
            4, "Asia",
            5, "Oceania"
    );

    public enum Role{
        USER, ADMIN
    }
}


