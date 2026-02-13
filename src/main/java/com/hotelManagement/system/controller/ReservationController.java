package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.ReservationCreateDTO;
import com.hotelManagement.system.dto.ReservationResponseDTO;
import com.hotelManagement.system.dto.ReservationUpdateDTO;
import com.hotelManagement.system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ReservationController {

    private final ReservationService reservationService;

    // CSV: POST /api/reservation/post
    @PostMapping("/api/reservation/post")
    public ResponseEntity<ReservationResponseDTO> create(@Valid @RequestBody ReservationCreateDTO dto) {
        return ResponseEntity.status(201).body(reservationService.create(dto));
    }

    // CSV: GET /api/reservation/all
    @GetMapping("/api/reservation/all")
    public ResponseEntity<List<ReservationResponseDTO>> getAll() {
        return ResponseEntity.ok(reservationService.getAll());
    }

    // CSV: GET /api/reservation/{reservation_id}
    @GetMapping("/api/reservation/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> get(@PathVariable("reservationId") Integer reservationId) {
        return ResponseEntity.ok(reservationService.get(reservationId));
    }

    // CSV: GET /api/reservations/date-range?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
    @GetMapping("/api/reservations/date-range")
    public ResponseEntity<List<ReservationResponseDTO>> getByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reservationService.getByDateRange(startDate, endDate));
    }

    // CSV: PUT /api/reservation/update/{reservation_id}
    @PutMapping("/api/reservation/update/{reservationId}")
    public ResponseEntity<ReservationResponseDTO> update(
            @PathVariable("reservationId") Integer reservationId,
            @Valid @RequestBody ReservationUpdateDTO dto) {
        return ResponseEntity.ok(reservationService.update(reservationId, dto));
    }

    // CSV: DELETE /api/reservation/{reservation_id}
    @DeleteMapping("/api/reservation/{reservationId}")
    public ResponseEntity<Void> delete(@PathVariable("reservationId") Integer reservationId) {
        reservationService.delete(reservationId);
        return ResponseEntity.noContent().build();
    }
}