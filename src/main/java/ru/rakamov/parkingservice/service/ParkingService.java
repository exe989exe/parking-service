package ru.rakamov.parkingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.rakamov.parkingservice.entity.ParkingEntity;
import ru.rakamov.parkingservice.record.ParkingReport;
import ru.rakamov.parkingservice.repository.ParkingRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRecordRepository;

    @Value("${parking.total.spaces:100}")
    private int totalSpaces;

    public LocalDateTime registerEntry(String carNumber, String vehicleType) {
        if (parkingRecordRepository.findByCarNumberAndExitTimeIsNull(carNumber).isPresent()) {
            throw new IllegalArgumentException("Car already parked");
        }
        ParkingEntity record = new ParkingEntity();
        record.setCarNumber(carNumber);
        record.setEntryTime(LocalDateTime.now());
        record.setVehicleType(vehicleType);
        parkingRecordRepository.save(record);
        return record.getEntryTime();
    }

    public LocalDateTime registerExit(String carNumber) {
        ParkingEntity record = parkingRecordRepository.findByCarNumberAndExitTimeIsNull(carNumber)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        record.setExitTime(LocalDateTime.now());
        parkingRecordRepository.save(record);
        return record.getExitTime();
    }

    public ParkingReport generateReport(LocalDateTime startDate, LocalDateTime endDate) {
        Long occupied = parkingRecordRepository.countOccupiedSpaces(startDate, endDate);
        int free = totalSpaces - occupied.intValue();
        Double avgSeconds = parkingRecordRepository.calculateAverageParkingTime(startDate, endDate);
        double avgHours = avgSeconds != null ? avgSeconds / 3600 : 0;
        return new ParkingReport(occupied, free, avgHours);
    }

}
