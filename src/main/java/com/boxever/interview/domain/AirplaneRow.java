package com.boxever.interview.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AirplaneRow {

    private Integer rowId;
    private boolean rowMerged;

    private List<SeatOccupant> windowSeatOccupants = new ArrayList<>();
    private List<SeatOccupant> regularSeatOccupants = new ArrayList<>();

    public void addOccupantsToSeats(List<SeatOccupant> group, Integer rowId) {
        this.rowId = rowId;

        group.forEach(person -> {
            if (person.isWindowOccupant()) {
                windowSeatOccupants.add(person);
            } else {
                regularSeatOccupants.add(person);
            }
        });
    }

    public boolean mergeOccupantsIntoRow(List<AirplaneRow> rowToMerge, Integer rowSize) {
        Integer maxWindows = 2;
        Integer maxRegularSeats = rowSize - maxWindows;
        AtomicBoolean successfulMerge = new AtomicBoolean(false);

        rowToMerge.forEach(row -> {
            if (!row.getWindowSeatOccupants().isEmpty()) {
                row.getWindowSeatOccupants().forEach(actionRow -> {
                    if (windowSeatOccupants.size() < maxWindows) {
                        windowSeatOccupants.add(actionRow);
                        successfulMerge.set(true);
                    } else if (regularSeatOccupants.size() < maxRegularSeats) {
                        regularSeatOccupants.add(actionRow);
                        successfulMerge.set(true);
                    } else {
                        System.out.println("DEAL WITH EXTRA PASSENGER 1");
                    }
                });
            }

            if (!row.getRegularSeatOccupants().isEmpty()) {
                row.getRegularSeatOccupants().forEach(actionRow -> {
                    if (regularSeatOccupants.size() < maxRegularSeats) {
                        regularSeatOccupants.add(actionRow);
                        successfulMerge.set(true);
                    } else if (windowSeatOccupants.size() < maxWindows) {
                        windowSeatOccupants.add(actionRow);
                        successfulMerge.set(true);
                    } else {
                        System.out.println("DEAL WITH EXTRA PASSENGER 2");
                    }
                });
            }

            if (successfulMerge.get())
                row.setRowMerged(true);
        });

        System.out.println("test");
        return successfulMerge.get();
    }

    public boolean isFull() {
        return windowSeatOccupants.isEmpty() && regularSeatOccupants.isEmpty();
    }

    public Integer getSeatsOccupied() {
        return windowSeatOccupants.size() + regularSeatOccupants.size();
    }
}
