package com.boxever.interview.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<String> getAllGroupMembers() {
        List<String> regularSeats = getNormalSeatOccupants();
        List<String> windowSeats = getWindowSeatOccupants();

        return Stream.of(regularSeats, windowSeats).flatMap(List::stream).collect(Collectors.toList());
    }
}
