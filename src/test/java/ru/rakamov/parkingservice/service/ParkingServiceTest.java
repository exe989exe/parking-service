package ru.rakamov.parkingservice.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.rakamov.parkingservice.entity.ParkingEntity;
import ru.rakamov.parkingservice.record.ParkingReport;
import ru.rakamov.parkingservice.repository.ParkingRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ParkingServiceTest {

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ParkingService parkingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterEntry() {
        // Подготовка данных
        String carNumber = "ABC123";
        String vehicleType = "легковой";

        // Мокируем репозиторий
        when(parkingRepository.findByCarNumberAndExitTimeIsNull(carNumber)).thenReturn(Optional.empty());
        when(parkingRepository.save(any(ParkingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Вызов метода
        LocalDateTime entryTime = parkingService.registerEntry(carNumber, vehicleType);

        // Проверки
        assertNotNull(entryTime);
        verify(parkingRepository, times(1)).save(any(ParkingEntity.class));
    }

    @Test
    void testRegisterExit() {
        // Подготовка данных
        String carNumber = "ABC123";
        ParkingEntity record = new ParkingEntity();
        record.setCarNumber(carNumber);
        record.setEntryTime(LocalDateTime.now());

        // Мокируем репозиторий
        when(parkingRepository.findByCarNumberAndExitTimeIsNull(carNumber)).thenReturn(Optional.of(record));
        when(parkingRepository.save(any(ParkingEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Вызов метода
        LocalDateTime exitTime = parkingService.registerExit(carNumber);

        // Проверки
        assertNotNull(exitTime);
        assertEquals(record.getExitTime(), exitTime);
        verify(parkingRepository, times(1)).save(any(ParkingEntity.class));
    }

    @Test
    void testGenerateReport() {
        // Подготовка данных
        LocalDateTime startDate = LocalDateTime.of(2023, 10, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 2, 0, 0);

        // Мокируем репозиторий
        when(parkingRepository.countOccupiedSpaces(startDate, endDate)).thenReturn(5L);
        when(parkingRepository.calculateAverageParkingTime(startDate, endDate)).thenReturn(3600.0); // 1 час в секундах

        // Вызов метода
        ParkingReport report = parkingService.generateReport(startDate, endDate);

        // Проверки
        assertNotNull(report);
        assertEquals(5L, report.occupiedSpaces());
        assertEquals(95, report.freeSpaces()); // Предположим, что всего 100 мест
        assertEquals(1.0, report.averageParkingTimeHours()); // 3600 секунд = 1 час
    }
}