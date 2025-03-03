package ru.rakamov.parkingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rakamov.parkingservice.record.ParkingEntryRequest;
import ru.rakamov.parkingservice.record.ParkingExitRequest;
import ru.rakamov.parkingservice.record.ParkingReport;
import ru.rakamov.parkingservice.service.ParkingService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/entry")
    public ResponseEntity<Map<String, LocalDateTime>> registerEntry(@RequestBody ParkingEntryRequest request) {
        LocalDateTime entryTime = parkingService.registerEntry(request.carNumber(), request.vehicleType());
        return ResponseEntity.ok(Collections.singletonMap("entryTime", entryTime));
    }

    @PostMapping("/exit")
    public ResponseEntity<Map<String, LocalDateTime>> registerExit(@RequestBody ParkingExitRequest request) {
        LocalDateTime exitTime = parkingService.registerExit(request.carNumber());
        return ResponseEntity.ok(Collections.singletonMap("exitTime", exitTime));
    }

    @GetMapping("/report")
    public ResponseEntity<ParkingReport> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end_date) {
        return ResponseEntity.ok(parkingService.generateReport(start_date, end_date));
    }

}