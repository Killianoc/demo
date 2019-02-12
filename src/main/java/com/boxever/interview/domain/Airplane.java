package com.boxever.interview.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Airplane {

    private Integer seatsInRow;
    private Integer numberOfRowsInPlane;

    private List<AirplaneRow> airplaneRows = new ArrayList<>();
    private List<TravelGroups> potentialTravelGroups = new ArrayList<>();

}
