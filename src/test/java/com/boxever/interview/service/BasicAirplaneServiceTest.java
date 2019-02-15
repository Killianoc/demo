package com.boxever.interview.service;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.SeatOccupant;
import com.boxever.interview.service.impl.BasicAirplaneService;
import com.boxever.interview.util.InputFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BasicAirplaneServiceTest {

    private BasicAirplaneService airplaneService = new BasicAirplaneService();

    @Test
    public void testAirplaneService() throws Exception {
        Airplane airplane = InputFileReader.readInputFile(Paths.get("src/test/resources/InputFiles/BasicInputFile.txt"));
        Airplane sortedAirplane = airplaneService.assignAirplaneSeats(airplane);

        List<List<SeatOccupant>> airplaneRows = sortedAirplane.getAirplaneRows().stream()
                .flatMap(item -> Stream.of(item.getRegularSeatOccupants(), item.getWindowSeatOccupants()))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());

        Assert.assertNotNull(airplaneRows);
        Assert.assertEquals("Row one window seats mapped correctly",
                1L, (long)airplaneRows.get(1).get(0).getOccupantId());
        Assert.assertEquals("Row one window seats mapped correctly",
                8L, (long)airplaneRows.get(1).get(1).getOccupantId());
        Assert.assertEquals("Row three mapped correctly",
                11L, (long)airplaneRows.get(4).get(0).getOccupantId());
        Assert.assertEquals("Row three mapped correctly",
                12L, (long)airplaneRows.get(4).get(1).getOccupantId());
    }

    @Test
    public void testAirplaneServiceScenarioTwo() throws Exception {
        Airplane airplane = InputFileReader.readInputFile(Paths.get("src/test/resources/InputFiles/BasicInputFile2.txt"));
        airplaneService.assignAirplaneSeats(airplane);

        List<List<SeatOccupant>> airplaneRows = airplane.getAirplaneRows().stream()
                .flatMap(item -> Stream.of(item.getRegularSeatOccupants(), item.getWindowSeatOccupants()))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());

        Assert.assertEquals("Row one mapped correctly",
                3L, (long)airplaneRows.get(0).get(0).getOccupantId());
        Assert.assertEquals("Row one mapped correctly",
                4L, (long)airplaneRows.get(0).get(1).getOccupantId());
        Assert.assertEquals("Row one mapped correctly",
                5L, (long)airplaneRows.get(0).get(2).getOccupantId());
        Assert.assertEquals("Row two mapped window seats correctly",
                2L, (long)airplaneRows.get(2).get(0).getOccupantId());
    }

    @Test
    public void testAirplaneFullyBookedWithPassengersMissingOut() throws Exception {
        Path inputPath = Paths.get("src/test/resources/InputFiles/BasicInputFile_fullFlight.txt");
        Airplane airplane = InputFileReader.readInputFile(inputPath);
        airplaneService.assignAirplaneSeats(airplane);

        airplane.getAirplaneRows().stream()
                .flatMap(item -> Stream.of(item.getRegularSeatOccupants(), item.getWindowSeatOccupants()))
                .filter(item -> !item.isEmpty())
                .forEach(item -> {
                    item.forEach(item2 -> {
                        Assert.assertNotEquals(17L, (long) item2.getOccupantId());
                        Assert.assertNotEquals(18L, (long) item2.getOccupantId());
                        Assert.assertNotEquals(19L, (long) item2.getOccupantId());
                        Assert.assertNotEquals(20L, (long) item2.getOccupantId());
                    });
                });
    }

    @Test
    public void testShouldMapExtraOccupantsToPlane() throws Exception {
        Path inputPath = Paths.get("src/test/resources/InputFiles/InputFile_extraPassengers.txt");
        Airplane airplane = InputFileReader.readInputFile(inputPath);
        airplaneService.assignAirplaneSeats(airplane);

        List<SeatOccupant> windowSeatOccupants = airplane.getAirplaneRows().get(1).getWindowSeatOccupants();

        Assert.assertEquals(5L, (long)windowSeatOccupants.get(0).getOccupantId());
        Assert.assertEquals(6L, (long)windowSeatOccupants.get(1).getOccupantId());
    }

    @Test
    public void testShouldMapExtraOccupantToNewRow() throws Exception {
        Path inputPath = Paths.get("src/test/resources/InputFiles/InputFile_extraPassengers2.txt");
        Airplane airplane = InputFileReader.readInputFile(inputPath);
        airplaneService.assignAirplaneSeats(airplane);

        List<SeatOccupant> windowSeatOccupants = airplane.getAirplaneRows().get(2).getRegularSeatOccupants();

        Assert.assertEquals(9L, (long)windowSeatOccupants.get(0).getOccupantId());
    }
}
