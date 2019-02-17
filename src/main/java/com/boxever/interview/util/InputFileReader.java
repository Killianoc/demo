package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.TravelGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
public class InputFileReader {

    private static final Pattern spacePattern = Pattern.compile("\\s+");

    public static Airplane readInputFile(Path inputFilePath) throws IOException {
        AtomicBoolean isFirstLine = new AtomicBoolean(true);
        AtomicInteger rowCount = new AtomicInteger(0);
        Airplane airplane = new Airplane();

        if (!inputFilePath.toFile().exists()) {
            throw new RuntimeException("File does not exist or incorrect path was entered");
        }

        try (Stream<String> inputLineStream = Files.lines(inputFilePath, Charset.forName("UTF-8"))) {
            inputLineStream.forEach(line -> {
                if (isFirstLine.get()) {
                    mapAirplaneSeatParameters(airplane,
                            Arrays.asList(line.split(spacePattern.pattern())));
                    isFirstLine.set(false);
                } else {
                    List<String> inputGroupLine = Arrays.asList(line.split(spacePattern.pattern()));
                    TravelGroup travelGroup =
                            new TravelGroup(airplane.getSeatsInRow()).addOccupantsToSeats(inputGroupLine);
                    airplane.getPotentialTravelGroups().add(travelGroup);
                    rowCount.incrementAndGet();
                }
            });
        }

        if (airplane.getPotentialTravelGroups().isEmpty()) {
            throw new RuntimeException("Invalid airplane parameters were passed in the input file.");
        }

        return airplane;
    }

    private static void mapAirplaneSeatParameters(Airplane airplane, List<String> planeParameters) {
        if (planeParameters.size() != 2) {
            throw new RuntimeException("Invalid plane seat and row parameters passed. There were not exactly two parameters.");
        }
        try {
            Integer seatsInRow = Integer.parseInt(planeParameters.get(0));
            Integer rowsInPlane = Integer.parseInt(planeParameters.get(1));

            if (seatsInRow <= 0 || rowsInPlane <= 0) {
                throw new RuntimeException("Plane seat and row parameters cannot be less than or equal to zero.");
            }

            airplane.setSeatsInRow(seatsInRow);
            airplane.setNumberOfRowsInPlane(rowsInPlane);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid parameters. First line must be numbers.");
        }
    }
}
