package com.boxever.interview.util;

import com.boxever.interview.domain.Airplane;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class InputFileReaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testShouldReadLegitimateInputFIle() throws Exception {
        Path inputFile = Paths.get("src/test/resources/InputFiles/BasicInputFile.txt");
        Airplane airplane = InputFileReader.readInputFile(inputFile);

        Assert.assertTrue(airplane.getNumberOfRowsInPlane().equals(4));
    }

    @Test(expected = RuntimeException.class)
    public void testWithInvalidFilePathShouldThrowException() throws Exception {
        Path inputFile = Paths.get("does/not/exist");
        InputFileReader.readInputFile(inputFile);
    }

    @Test(expected = RuntimeException.class)
    public void testWithIncompletePlaneDataShouldThrowException() throws Exception {
        Path inputFile = Paths.get("src/test/resources/InputFiles/invalidInput_emptyPlane.txt");
        InputFileReader.readInputFile(inputFile);
    }

    @Test
    public void testWithInvalidSeatParametersThrowsException() throws Exception {
        Path invalidFile = Paths.get("src/test/resources/InputFiles/invalidInput_invalidSeatParams.txt");

        exception.expect(RuntimeException.class);
        exception.expectMessage("Invalid plane seat and row parameters passed.");
        InputFileReader.readInputFile(invalidFile);
    }
}

