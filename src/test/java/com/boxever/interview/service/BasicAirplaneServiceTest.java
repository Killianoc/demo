package com.boxever.interview.service;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.domain.SeatOccupant;
import com.boxever.interview.service.impl.BasicAirplaneService;
import com.boxever.interview.util.InputFileReader;
import org.junit.Assert;
import org.junit.Test;

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
}
