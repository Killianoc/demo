package com.boxever.interview.service.impl;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.AirplaneRow;
import com.boxever.interview.domain.SeatOccupant;
import com.boxever.interview.domain.TravelGroup;
import com.boxever.interview.service.SatisfactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

@Service
@Slf4j
public class CustomerSatisfactionService implements SatisfactionService {

    private static final Integer BARELY_SATISFIED = 25;
    private static final Integer SOMEWHAT_SATISFIED = 50;
    private static final Integer FULLY_SATISFIED = 100;

    BiPredicate<TravelGroup, SeatOccupant> isSeatOccupantInRow = (group, occupant) -> {
        Integer occupantId = occupant.getOccupantId();
        boolean isOccupantRegular = group.getNormalSeatOccupants().stream().anyMatch(coTraveller ->
                coTraveller.contains(Integer.toString(occupantId)));
        boolean isOccupantWindow = group.getWindowSeatOccupants().stream().anyMatch(coTraveller ->
                coTraveller.contains(Integer.toString(occupantId)));
        return isOccupantRegular || isOccupantWindow;
    };

    public Double calculateSatisfaction(Airplane airplane) {
        List<TravelGroup> potentialTravelGroups = airplane.getPotentialTravelGroups();
        List<AirplaneRow> assignedSeats = airplane.getAirplaneRows();

        assignedSeats.forEach(airplaneRow -> {
            airplaneRow.getRegularSeatOccupants().forEach(occupant -> {
                Optional<TravelGroup> originalGroup = potentialTravelGroups.stream()
                        .filter(group -> isSeatOccupantInRow.test(group, occupant))
                        .findFirst();
                if (!originalGroup.isPresent()) {
                    log.error("Passenger " + occupant.getOccupantId() + " has come from nowhere!");
                    return;
                }
                querySatisfaction(airplaneRow, originalGroup.get(), occupant);
            });

            airplaneRow.getWindowSeatOccupants().forEach(occupant -> {
                Optional<TravelGroup> originalGroup = potentialTravelGroups.stream()
                        .filter(group -> isSeatOccupantInRow.test(group, occupant))
                        .findFirst();
                if (!originalGroup.isPresent()) {
                    log.error("Passenger " + occupant.getOccupantId() + " has come from nowhere!");
                    return;
                }
                querySatisfaction(airplaneRow, originalGroup.get(), occupant);
            });
        });

        return calculateSatisfactionRate(airplane);
    }

    private void querySatisfaction(AirplaneRow row, TravelGroup travelGroup, SeatOccupant occupant) {
        AtomicBoolean isNotWithGroup = new AtomicBoolean(false);
        List<String> originalGroup = travelGroup.getAllGroupMembers();

        originalGroup.stream()
                .map(this::getIndividual)
                .filter(member -> !member.equals(occupant.getOccupantId()))
                .forEach(originalMember -> {

            if (!row.doesRowContainIndividual(originalMember)) {
                isNotWithGroup.set(true);
                if (!occupant.isWindowOccupant() && originalMember.isWindowOccupant()) {
                    occupant.setSatisfactionPercent(BARELY_SATISFIED);
                } else {
                    occupant.setSatisfactionPercent(SOMEWHAT_SATISFIED);
                }
            }
        });

        if (!isNotWithGroup.get()) {
            occupant.setSatisfactionPercent(FULLY_SATISFIED);
        }
    }

    private SeatOccupant getIndividual(String individual) {
        SeatOccupant.SeatOccupantBuilder occupant = SeatOccupant.builder();
        if (individual.contains("W")) {
            return occupant.isWindowOccupant(true)
                    .occupantId(Integer.parseInt(individual.substring(0, individual.length() - 1)))
                    .build();
        }
        return occupant.occupantId(Integer.parseInt(individual)).build();
    }

    private Double calculateSatisfactionRate(Airplane airplane) {
        Double maxSatisfaction = Double.valueOf(airplane.getNumberOfRowsInPlane() * airplane.getSeatsInRow()) * 100;
        Double currentSatisfactionRate = airplane.getAirplaneRows().stream()
                .mapToDouble(row -> row.getAllSeatOccupants().stream().mapToDouble(SeatOccupant::getSatisfactionPercent).sum())
                .sum();

        return (currentSatisfactionRate / maxSatisfaction) * 100;
    }
}
