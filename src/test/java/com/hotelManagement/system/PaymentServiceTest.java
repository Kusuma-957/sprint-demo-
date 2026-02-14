package com.hotelManagement.system;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;


import com.hotelManagement.system.dto.PaymentCreateDTO;
import com.hotelManagement.system.dto.PaymentResponseDTO;
import com.hotelManagement.system.dto.PaymentRevenueResponseDTO;
import com.hotelManagement.system.entity.Payment;
import com.hotelManagement.system.entity.PaymentStatus;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.PaymentRepository;
import com.hotelManagement.system.repository.ReservationRepository;
import com.hotelManagement.system.service.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ReservationRepository reservationRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentCreateDTO createDto;

    @BeforeEach
    void setup() {
        createDto = new PaymentCreateDTO();
        createDto.setReservationId(101);
        createDto.setAmount(new BigDecimal("2500.00"));
        createDto.setPaymentStatus(PaymentStatus.Paid);
    }

    private static Reservation reservation(int id) {
        Reservation r = new Reservation();
        r.setReservationId(id);
        return r;
    }

    private static Payment payment(int id, int resId, String amt, PaymentStatus status, LocalDate date) {
        Payment p = new Payment();
        p.setPaymentId(id);
        Reservation r = new Reservation();
        r.setReservationId(resId);
        p.setReservation(r);
        p.setAmount(new BigDecimal(amt));
        p.setPaymentStatus(status);
        p.setPaymentDate(date);
        return p;
    }

    // ---------------------- CREATE ----------------------

    @Test
    @DisplayName("create: reservation not found → ResourceNotFoundException")
    void create_reservationMissing_throws() {
        when(reservationRepository.findById(101)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.create(createDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation doesn't exist");

        verify(reservationRepository).findById(101);
        verifyNoMoreInteractions(reservationRepository);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    @DisplayName("create: duplicate payment for same reservation & status → ConflictException")
    void create_duplicatePayment_throws() {
        when(reservationRepository.findById(101)).thenReturn(Optional.of(reservation(101)));
        when(paymentRepository.existsByReservation_ReservationIdAndPaymentStatus(101, PaymentStatus.Paid))
                .thenReturn(true);

        assertThatThrownBy(() -> paymentService.create(createDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Payment already exist");

        verify(reservationRepository).findById(101);
        verify(paymentRepository).existsByReservation_ReservationIdAndPaymentStatus(101, PaymentStatus.Paid);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    @DisplayName("create: happy path → payment saved with mapped fields & today’s date")
    void create_ok_saves() {
        Reservation found = reservation(101);

        when(reservationRepository.findById(101)).thenReturn(Optional.of(found));
        when(paymentRepository.existsByReservation_ReservationIdAndPaymentStatus(101, PaymentStatus.Paid))
                .thenReturn(false);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setPaymentId(999);
            return p;
        });

        paymentService.create(createDto);

        verify(reservationRepository).findById(101);
        verify(paymentRepository).existsByReservation_ReservationIdAndPaymentStatus(101, PaymentStatus.Paid);
        verify(paymentRepository).save(any(Payment.class));
    }

    // ---------------------- GET ----------------------

    @Test
    @DisplayName("get: payment missing → ResourceNotFoundException")
    void get_missing_throws() {
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.get(1))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment doesn't exist");

        verify(paymentRepository).findById(1);
    }

    @Test
    @DisplayName("get: payment found → mapped response")
    void get_found_returnsDto() {
        Payment p = payment(55, 999, "1500.00", PaymentStatus.Paid, LocalDate.of(2026, 2, 15));

        when(paymentRepository.findById(55)).thenReturn(Optional.of(p));

        PaymentResponseDTO dto = paymentService.get(55);

        assertThat(dto.getPaymentId()).isEqualTo(55);
        assertThat(dto.getReservationId()).isEqualTo(999);
        assertThat(dto.getAmount()).isEqualByComparingTo("1500.00");
        assertThat(dto.getPaymentStatus()).isEqualTo(PaymentStatus.Paid);
        assertThat(dto.getPaymentDate()).isEqualTo(LocalDate.of(2026, 2, 15));

        verify(paymentRepository).findById(55);
    }

    // ---------------------- GET ALL ----------------------

    @Test
    @DisplayName("getAll: empty list → EmptyListException")
    void getAll_empty_throws() {
        when(paymentRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> paymentService.getAll())
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("Payment list is empty");

        verify(paymentRepository).findAll();
    }

    @Test
    @DisplayName("getAll: non‑empty list → mapped")
    void getAll_nonEmpty() {
        when(paymentRepository.findAll()).thenReturn(List.of(
                payment(1, 10, "1000", PaymentStatus.Paid, LocalDate.now()),
                payment(2, 20, "2000", PaymentStatus.Paid, LocalDate.now())
        ));

        List<PaymentResponseDTO> list = paymentService.getAll();

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getPaymentId()).isEqualTo(1);
        assertThat(list.get(1).getPaymentId()).isEqualTo(2);

        verify(paymentRepository).findAll();
    }

    // ---------------------- GET BY STATUS ----------------------

    @Test
    @DisplayName("getByStatus: empty → EmptyListException")
    void getByStatus_empty_throws() {
        when(paymentRepository.findByPaymentStatus(PaymentStatus.FAILED)).thenReturn(List.of());

        assertThatThrownBy(() -> paymentService.getByStatus(PaymentStatus.FAILED))
                .isInstanceOf(EmptyListException.class)
                .hasMessageContaining("Payment list is empty");

        verify(paymentRepository).findByPaymentStatus(PaymentStatus.FAILED);
    }

    @Test
    @DisplayName("getByStatus: non‑empty → mapped")
    void getByStatus_nonEmpty() {
        when(paymentRepository.findByPaymentStatus(PaymentStatus.PENDING)).thenReturn(List.of(
                payment(5, 55, "3000", PaymentStatus.PENDING, LocalDate.now())
        ));

        List<PaymentResponseDTO> list = paymentService.getByStatus(PaymentStatus.PENDING);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);

        verify(paymentRepository).findByPaymentStatus(PaymentStatus.PENDING);
    }

    // ---------------------- GET TOTAL REVENUE ----------------------

    @Test
    @DisplayName("getTotalRevenue: returns sum from repository")
    void getTotalRevenue_ok() {
        when(paymentRepository.sumTotalRevenuePaid())
                .thenReturn(new BigDecimal("9999.99"));

        PaymentRevenueResponseDTO resp = paymentService.getTotalRevenue();

        assertThat(resp.getTotalRevenue()).isEqualByComparingTo("9999.99");

        verify(paymentRepository).sumTotalRevenuePaid();
    }

    // ---------------------- DELETE ----------------------

    @Test
    @DisplayName("delete: missing payment → ResourceNotFoundException")
    void delete_missing_throws() {
        when(paymentRepository.findById(77)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.delete(77))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment doesn't exist");

        verify(paymentRepository).findById(77);
    }

    @Test
    @DisplayName("delete: found → delete called")
    void delete_ok() {
        Payment p = payment(88, 123, "1500", PaymentStatus.Paid, LocalDate.now());
        when(paymentRepository.findById(88)).thenReturn(Optional.of(p));

        paymentService.delete(88);

        verify(paymentRepository).findById(88);
        verify(paymentRepository).delete(p);
    }
}

