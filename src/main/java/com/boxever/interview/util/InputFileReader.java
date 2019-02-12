package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.TravelGroups;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class InputFileReader {

    private static final Pattern spacePattern = Pattern.compile("\\s+");

    public static Airplane readInputFile(Path inputFilePath) throws IOException {
        AtomicBoolean isFirstLine = new AtomicBoolean(true);
        AtomicInteger rowCount = new AtomicInteger(0);
        Airplane airplane = new Airplane();

        if (!inputFilePath.toFile().exists()) {
            throw new RuntimeException("File does not exist or incorrect path was entered");
        }

        Files.lines(inputFilePath, Charset.forName("UTF-8")).forEach(line -> {
            if (isFirstLine.get()) {
                mapAirplaneSeatParameters(airplane,
                        Arrays.asList(line.split(spacePattern.pattern())));
                isFirstLine.set(false);
            } else {
                List<String> inputGroupLine = Arrays.asList(line.split(spacePattern.pattern()));
                TravelGroups travelGroup =
                        new TravelGroups(airplane.getSeatsInRow()).addOccupantsToSeats(inputGroupLine);
                airplane.getPotentialTravelGroups().add(travelGroup);
                rowCount.incrementAndGet();
            }
        });

        if (airplane.getPotentialTravelGroups().isEmpty()) {
            throw new RuntimeException("Invalid airplane parameters were passed in the input file.");
        }

        return airplane;
    }

    private static void mapAirplaneSeatParameters(Airplane airplane, List<String> planeParameters) {
        if (planeParameters.size() != 2) {
            throw new RuntimeException("Invalid plane seat and row parameters passed.");
        }
        airplane.setSeatsInRow(Integer.parseInt(planeParameters.get(0)));
        airplane.setNumberOfRowsInPlane(Integer.parseInt(planeParameters.get(1)));
    }
}
