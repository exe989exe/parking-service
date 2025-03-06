package ru.rakamov.parkingservice.service.impl;

import ru.rakamov.parkingservice.record.ParkingReport;

import java.time.LocalDateTime;

public interface ParkingServiceImpl {

    public LocalDateTime registerEntry(String carNumber, String vehicleType);

    public LocalDateTime registerExit(String carNumber);

    public ParkingReport generateReport(LocalDateTime startDate, LocalDateTime endDate);

}
