package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.AirplaneRow;
import com.boxever.interview.domain.SeatOccupant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class AirplaneSeatUtil {

    private static Predicate<AirplaneRow> canRowBeMerged = row -> row.getSeatsOccupied() <= 2;

    public static void sortAirplaneSeats(Airplane airplane) {
        List<AirplaneRow> passedRows = new ArrayList<>();
        Predicate<AirplaneRow> hasRowBeenProcessed = (row) -> passedRows.contains(row);

        while (airplane.getAirplaneRows().size() > airplane.getNumberOfRowsInPlane()) {
            Optional<AirplaneRow> airplaneRow = airplane.getAirplaneRows().stream()
                    .filter(canRowBeMerged.and(hasRowBeenProcessed.negate()))
                    .findAny();
            if (!airplaneRow.isPresent()) {
                break;
            }
            boolean hasMerged = findExtraCapacityAndMergeRow(airplane, airplaneRow.get());
            if (hasMerged) {
                passedRows.add(airplaneRow.get());
            }
        }
    }

    private static boolean findExtraCapacityAndMergeRow(Airplane airplane, AirplaneRow rowToMerge) {
        Integer neededCapacity = rowToMerge.getSeatsOccupied();
        boolean hasMergeOccurred = false;

        for (int i = 0; i < airplane.getAirplaneRows().size(); i++) {
            if (airplane.getAirplaneRows().get(i).isRowMerged()) {
                continue;
            } else if (airplane.getAirplaneRows().get(i).equals(rowToMerge)) {
                continue;
            }

            if ((airplane.getAirplaneRows().get(i).getSeatsOccupied() + neededCapacity)
                    <= airplane.getSeatsInRow()) {
                if (airplane.getAirplaneRows().get(i)
                        .mergeOccupantsIntoRow(Arrays.asList(rowToMerge), airplane.getSeatsInRow())) {
                    hasMergeOccurred = true;
                    break;
                }
            }
        }
        return hasMergeOccurred;
    }

    public static String printAirplaneLayout(Airplane airplane) {
        StringBuffer allOutputRows = new StringBuffer();
        log.info("Outputting final plane layout");
        airplane.getAirplaneRows().forEach(row -> {
            Deque<Integer> windowRows = row.getWindowSeatOccupants()
                    .stream().map(SeatOccupant::getOccupantId)
                    .collect(Collectors.toCollection(ArrayDeque::new));
            Deque<Integer> regularRows = row.getRegularSeatOccupants()
                    .stream().map(SeatOccupant::getOccupantId)
                    .collect(Collectors.toCollection(ArrayDeque::new));

            String rowString = generateRowString(windowRows, regularRows);

            // The row did not have any occupants who specified a window seat preference
            if (StringUtils.isEmpty(rowString)) {
                rowString = generateRowString(regularRows, windowRows);
            }
            log.info(rowString);
            allOutputRows.append(rowString).append(",");
        });
        return allOutputRows.toString();
    }

    private static String generateRowString(Deque<Integer> firstDeque, Deque<Integer> secondDeque) {
        StringBuffer rowString = new StringBuffer();
        while (firstDeque.size() > 0) {
            rowString.append(firstDeque.poll()).append(" ");
            while (secondDeque.size() > 0) {
                rowString.append(secondDeque.poll()).append(" ");
            }
        }
        return rowString.toString();
    }


}
