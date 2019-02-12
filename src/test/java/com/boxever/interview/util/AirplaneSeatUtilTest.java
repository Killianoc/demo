package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.service.impl.BasicAirplaneService;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AirplaneSeatUtilTest {

    @Test
    public void testShouldOutputPlaneCorrectly() throws Exception {

        Airplane airplane = InputFileReader.readInputFile(Paths.get("src/test/resources/InputFiles/BasicInputFile.txt"));
        new BasicAirplaneService().assignAirplaneSeats(airplane);
        String rows = AirplaneSeatUtil.printAirplaneLayout(airplane);
        List<String> rowList = Arrays.asList(rows.split(","));

        Assert.assertTrue(rowList.get(0).contains("1 2 3 8"));
        Assert.assertTrue(rowList.get(1).contains("4 5 6 7"));
        Assert.assertTrue(rowList.get(2).contains("11 9 10 12"));
        Assert.assertTrue(rowList.get(3).contains("13 15 16 14"));
    }
}