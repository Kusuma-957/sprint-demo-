package com.hotelManagement.system.controller;

import com.hotelManagement.system.dto.PaymentCreateDTO;
import com.hotelManagement.system.dto.PaymentResponseDTO;
import com.hotelManagement.system.dto.PaymentRevenueResponseDTO;
import com.hotelManagement.system.service.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    // POST /api/payment/post
    @PostMapping("/api/payment/post")
    public ResponseEntity<PaymentResponseDTO> create(@Valid @RequestBody PaymentCreateDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    // GET /api/payment/all
    @GetMapping("/api/payment/all")
    public ResponseEntity<List<PaymentResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // GET /api/payment/{payment_id}
    @GetMapping("/api/payment/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> get(@PathVariable("paymentId") Integer paymentId) {
        return ResponseEntity.ok(service.get(paymentId));
    }

    // GET /api/payments/status/{status}
    @GetMapping("/api/payments/status/{status}")
    public ResponseEntity<List<PaymentResponseDTO>> getByStatus(@PathVariable("status") String status) {
        return ResponseEntity.ok(service.getByStatus(status));
    }

    // GET /api/payments/total-revenue
    @GetMapping("/api/payments/total-revenue")
    public ResponseEntity<PaymentRevenueResponseDTO> getTotalRevenue() {
        return ResponseEntity.ok(service.getTotalRevenue());
    }

    // DELETE /api/payment/{payment_id}
    @DeleteMapping("/api/payment/{paymentId}")
    public ResponseEntity<Void> delete(@PathVariable("paymentId") Integer paymentId) {
        service.delete(paymentId);
        return ResponseEntity.noContent().build();
    }
}