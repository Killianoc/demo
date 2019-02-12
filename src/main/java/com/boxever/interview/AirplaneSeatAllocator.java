package com.boxever.interview;

import com.boxever.interview.domain.Airplane;
import com.boxever.interview.service.AirplaneService;
import com.boxever.interview.util.InputFileReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

@SpringBootApplication
@Slf4j
public class AirplaneSeatAllocator implements CommandLineRunner {

	@Autowired
	private AirplaneService basicAirplaneService;

	public static void main(String[] args) {
		SpringApplication.run(AirplaneSeatAllocator.class, args);
	}

	public void run(String... args) throws Exception {
		if (args.length != 1) {
			log.error("Invalid arguments provided. Usage: InputFilePath.txt");
		}

		log.info("Attempting to allocate airplane seats based on input file " + args[0]);
		Airplane airplane = InputFileReader.readInputFile(Paths.get(args[0]));
		basicAirplaneService.assignAirplaneSeats(airplane);
	}

}

