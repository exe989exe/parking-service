package ru.rakamov.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.rakamov.parkingservice.entity.ParkingEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ParkingRepository extends JpaRepository<ParkingEntity, Long> {
    Optional<ParkingEntity> findByCarNumberAndExitTimeIsNull(String carNumber);

    @Query("SELECT COUNT(p) FROM ParkingEntity p WHERE p.entryTime <= :endDate AND (p.exitTime >= :startDate OR p.exitTime IS NULL)")
    Long countOccupiedSpaces(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (exit_time - entry_time))) FROM parking_records WHERE exit_time BETWEEN :startDate AND :endDate", nativeQuery = true)
    Double calculateAverageParkingTime(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
