package ru.rakamov.parkingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.rakamov.parkingservice.record.ParkingEntryRequest;
import ru.rakamov.parkingservice.record.ParkingExitRequest;
import ru.rakamov.parkingservice.record.ParkingReport;
import ru.rakamov.parkingservice.service.ParkingService;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingService parkingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterEntry() throws Exception {
        // Подготовка данных
        String carNumber = "ABC123";
        String vehicleType = "легковой";
        LocalDateTime entryTime = LocalDateTime.now();

        // Мокируем сервис
        when(parkingService.registerEntry(carNumber, vehicleType)).thenReturn(entryTime);

        // Вызов API
        mockMvc.perform(post("/api/v1/parking/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParkingEntryRequest(carNumber, vehicleType))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryTime").exists());
    }

    @Test
    void testRegisterExit() throws Exception {
        // Подготовка данных
        String carNumber = "ABC123";
        LocalDateTime exitTime = LocalDateTime.now();

        // Мокируем сервис
        when(parkingService.registerExit(carNumber)).thenReturn(exitTime);

        // Вызов API
        mockMvc.perform(post("/api/v1/parking/exit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ParkingExitRequest(carNumber))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitTime").exists());
    }

    @Test
    void testGetReport() throws Exception {
        // Подготовка данных
        LocalDateTime startDate = LocalDateTime.of(2023, 10, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 10, 2, 0, 0);

        // Мокируем сервис
        when(parkingService.generateReport(startDate, endDate)).thenReturn(new ParkingReport(5L, 95, 1.0));

        // Вызов API
        mockMvc.perform(get("/api/v1/parking/report")
                        .param("start_date", startDate.toString())
                        .param("end_date", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupiedSpaces").value(5))
                .andExpect(jsonPath("$.freeSpaces").value(95))
                .andExpect(jsonPath("$.averageParkingTimeHours").value(1.0));
    }
}
