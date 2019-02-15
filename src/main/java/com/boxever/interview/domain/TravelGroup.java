package com.boxever.interview.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
public class TravelGroup {

    private static final String WINDOW_FLAG = "W";
    private Integer seatsPerRow;

    private List<String> windowSeatOccupants = new ArrayList<>();
    private List<String> normalSeatOccupants = new ArrayList<>();
    private List<String> extraOccupants = new ArrayList<>();

    public TravelGroup(Integer seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    private Consumer<String> occupantConsumer = (occupant) -> {
      if (occupant.endsWith(WINDOW_FLAG)) {
          if (windowSeatOccupants.size() < 2) {
              windowSeatOccupants.add(occupant);
          } else {
              extraOccupants.add(occupant);
          }
      } else {
          if (normalSeatOccupants.size() >= seatsPerRow) {
              extraOccupants.add(occupant);
          } else {
              normalSeatOccupants.add(occupant);
          }
      }
    };

    public TravelGroup addOccupantsToSeats(List<String> occupants) {
        occupants.forEach(occupantConsumer::accept);
        return this;
    }

    public TravelGroup addOccupantsAsExtras(List<String> occupants) {
        occupants.forEach(extraOccupants::add);
        return this;
    }
}
