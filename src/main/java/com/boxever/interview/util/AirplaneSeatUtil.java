package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.AirplaneRow;
import com.boxever.interview.domain.SeatOccupant;
import com.boxever.interview.domain.TravelGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public static Integer getNumberOfFreeSeats(Airplane airplane) {
        Integer currentSeats = airplane.getAirplaneRows().stream()
                .mapToInt(row -> airplane.getSeatsInRow()
                        - (row.getRegularSeatOccupants().size() + row.getWindowSeatOccupants().size()))
                .sum();
        Integer otherSeats = (airplane.getNumberOfRowsInPlane() - airplane.getAirplaneRows().size()) * airplane.getSeatsInRow();

        return currentSeats + otherSeats;
    }

    public static void accomodateExtraTravellers(List<String> extraGroups, Integer extraSeats,
                                                 Airplane airplane) {
        List<AirplaneRow> newRows = new ArrayList<>();
        List<String> mappedRows = new ArrayList<>();
        extraGroups.stream().limit(extraSeats).forEach(groups -> {
            airplane.getAirplaneRows().stream()
                    .filter(row -> !row.isFull())
                    .forEach(row -> {
                        if (mappedRows.contains(groups)) {
                            return;
                        }
                        if (airplane.getSeatsInRow() - (row.getRegularSeatOccupants().size()
                                + row.getWindowSeatOccupants().size()) != 0) {
                            mapGroupToRow(groups, row, airplane.getSeatsInRow());
                            mappedRows.add(groups);
                        }else if ((areOtherRowsAvailable(row, groups, airplane)) != null) {
                            mappedRows.add(groups);
                            return;
                        } else if (airplane.getAirplaneRows().size() < airplane.getNumberOfRowsInPlane()) {
                            AirplaneRow airplaneRow = new AirplaneRow();
                            if (groups.contains("W")) {
                                airplaneRow.getWindowSeatOccupants().add(SeatOccupant.builder().isWindowOccupant(true)
                                        .occupantId(Integer.parseInt(groups.substring(0, groups.length() - 1))).build());
                            } else {
                                airplaneRow.getRegularSeatOccupants().add(SeatOccupant.builder().isWindowOccupant(false)
                                        .occupantId(Integer.parseInt(groups)).build());
                            }
                            mappedRows.add(groups);
                            newRows.add(airplaneRow);
                        }
                    });
        });
        List<AirplaneRow> currentRows = airplane.getAirplaneRows();
        currentRows.addAll(newRows);
        airplane.setAirplaneRows(currentRows);
    }

    private static void mapGroupToRow(String travelGroup, AirplaneRow row, Integer seatsPerRow) {
        if (row.getWindowSeatOccupants().size() < 2) {
            mapOccupantToRow(row, travelGroup);
        } else if (row.getRegularSeatOccupants().size() < seatsPerRow - 2) {
            mapOccupantToRow(row, travelGroup);
        }
    }

    private static void mapOccupantToRow(AirplaneRow row, String occupant) {
        Integer occupantId = Integer.parseInt(occupant.contains("W")
                ? occupant.substring(0, occupant.length() - 1)
                : occupant);

        row.getWindowSeatOccupants().add(SeatOccupant.builder().isWindowOccupant(true)
                .occupantId(occupantId).build());
    }

    public static List<String> getExtraTravellers(Airplane airplane) {
        return airplane.getPotentialTravelGroups().stream()
                .flatMap(group -> group.getExtraOccupants().stream())
                .collect(Collectors.toList());
    }

    private static AirplaneRow areOtherRowsAvailable(AirplaneRow rowToSkip, String passenger, Airplane airplane) {
        Predicate<AirplaneRow> isRowElegible = (row) -> !row.isFull() && !row.equals(rowToSkip);
        AtomicBoolean isFurtherRowAvailable = new AtomicBoolean(false);
        List<AirplaneRow> rows = new ArrayList<>();
        airplane.getAirplaneRows().stream()
                .filter(isRowElegible)
                .forEach(item -> {
                    if (isFurtherRowAvailable.get())
                        return;
                    if (airplane.getSeatsInRow() - (item.getRegularSeatOccupants().size()
                            + item.getWindowSeatOccupants().size()) != 0) {
                        mapGroupToRow(passenger, item, airplane.getSeatsInRow());
                        rows.add(item);
                        isFurtherRowAvailable.set(true);
                    }
                });
        return rows.isEmpty() ? null : rows.get(0);
    }
}
