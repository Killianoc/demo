package com.boxever.interview.service;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.service.impl.BasicAirplaneService;
import com.boxever.interview.service.impl.CustomerSatisfactionService;
import com.boxever.interview.util.InputFileReader;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CustomerSatisfactionServiceTest {

    private CustomerSatisfactionService satisfactionService = new CustomerSatisfactionService();
    private BasicAirplaneService airplaneService = new BasicAirplaneService();

    @Test
    public void testSatisfactionRateIsCorrectForSatisfiedAirplane() throws Exception {
        Path inputPath = Paths.get("src/test/resources/InputFiles/BasicInputFile.txt");
        Airplane airplane = InputFileReader.readInputFile(inputPath);
        airplaneService.assignAirplaneSeats(airplane);

        Double satisfactionPercent = satisfactionService.calculateSatisfaction(airplane);

        Assert.assertTrue(satisfactionPercent.equals(100.0));
    }

    @Test
    public void testSatisfactionRateOnUnsatisfiedPlane() throws Exception {
        Path inputPath = Paths.get("src/test/resources/InputFiles/InputFile_unsatisfiedScenario.txt");
        Airplane airplane = InputFileReader.readInputFile(inputPath);
        airplaneService.assignAirplaneSeats(airplane);

        Double satisfactionPercent = satisfactionService.calculateSatisfaction(airplane);

        Assert.assertTrue(satisfactionPercent.equals(70.0));
    }
}
