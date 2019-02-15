package com.boxever.interview.service.impl;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.AirplaneRow;
import com.boxever.interview.domain.SeatOccupant;
import com.boxever.interview.domain.TravelGroup;
import com.boxever.interview.service.AirplaneService;
import com.boxever.interview.util.AirplaneSeatUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BasicAirplaneService implements AirplaneService {

    private Function<TravelGroup, List<SeatOccupant>> mapInputToOccupant = (person) -> {
        List<SeatOccupant> windowOccupants = person.getWindowSeatOccupants().stream()
                .map(seat -> mapSeatOccupants(true, false, seat))
                .collect(Collectors.toList());
        List<SeatOccupant> regularOccupants = person.getNormalSeatOccupants().stream()
                .map(seat -> mapSeatOccupants(false, false, seat))
                .collect(Collectors.toList());

        return Stream.of(windowOccupants, regularOccupants)
                .flatMap(List::stream).collect(Collectors.toList());
    };

    public Airplane assignAirplaneSeats(Airplane airplane) {
        List<AirplaneRow> airplaneRows = new ArrayList<>();
        List<List<SeatOccupant>> seatOccupants = airplane.getPotentialTravelGroups().stream()
                .map(mapInputToOccupant).collect(Collectors.toList());

        for (int i = 0; i < airplane.getPotentialTravelGroups().size(); i++) {
            AirplaneRow airplaneRow = new AirplaneRow();
            airplaneRow.addOccupantsToSeats(seatOccupants.get(i), i);
            airplaneRows.add(airplaneRow);
        }

        airplane.setAirplaneRows(airplaneRows);
        organiseAirplaneRows(airplane);
        List<AirplaneRow> sortedSeats = airplane.getAirplaneRows().stream()
                .filter(row -> !row.isRowMerged())
                .collect(Collectors.toList());
        airplane.setAirplaneRows(sortedSeats);

        //Check if the plane is not full and we have extra passengers, add them
        Integer numberOfFreeSeats = AirplaneSeatUtil.getNumberOfFreeSeats(airplane);
        if (numberOfFreeSeats > 0) {
            AirplaneSeatUtil.accomodateExtraTravellers(AirplaneSeatUtil.getExtraTravellers(airplane), numberOfFreeSeats,
                    airplane);
            organiseAirplaneRows(airplane);
        }
        // Strip any other rows that the flight has no room for
        Integer planeDivider = airplane.getAirplaneRows().size() < airplane.getNumberOfRowsInPlane()
                ? airplane.getAirplaneRows().size()
                : airplane.getNumberOfRowsInPlane();
        airplane.setAirplaneRows(airplane.getAirplaneRows().subList(0, planeDivider));

        AirplaneSeatUtil.printAirplaneLayout(airplane);
        return airplane;
    }

    private void organiseAirplaneRows(Airplane airplane) {
        AirplaneSeatUtil.sortAirplaneSeats(airplane);
    }

    private SeatOccupant mapSeatOccupants(boolean isWindow, boolean isExtra, String seat) {
        Integer occupantId = isWindow
                ? Integer.parseInt(seat.substring(0, seat.length() - 1))
                : Integer.parseInt(seat);
        return SeatOccupant.builder().isWindowOccupant(isWindow).isExtraOccupant(isExtra)
                .occupantId(occupantId).build();
    }
}
