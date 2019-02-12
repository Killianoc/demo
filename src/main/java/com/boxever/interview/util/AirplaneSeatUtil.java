package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.AirplaneRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
}
