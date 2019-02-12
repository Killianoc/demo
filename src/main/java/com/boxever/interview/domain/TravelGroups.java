package com.boxever.interview.domain;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class TravelGroups {

    private static final String WINDOW_FLAG = "W";
    private Integer seatsPerRow;

    private List<String> windowSeatOccupants = new ArrayList<>();
    private List<String> normalSeatOccupants = new ArrayList<>();
    private List<String> extraOccupants = new ArrayList<>();

    public TravelGroups(Integer seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    private Consumer<String> occupantConsumer = (occupant) -> {
      if (occupant.endsWith("W")) {
          if (windowSeatOccupants.size() < 2) {
              windowSeatOccupants.add(occupant);
          } else {
              extraOccupants.add(occupant);
          }
      } else {
          if (normalSeatOccupants.size() > seatsPerRow) {
              extraOccupants.add(occupant);
          } else {
              normalSeatOccupants.add(occupant);
          }
      }
    };

    public TravelGroups addOccupantsToSeats(List<String> occupants) {
        occupants.forEach(occupantConsumer::accept);
        return this;
    }
}
