package ru.rakamov.parkingservice.record;

public record ParkingReport(Long occupiedSpaces, Integer freeSpaces, Double averageParkingTimeHours) {}
