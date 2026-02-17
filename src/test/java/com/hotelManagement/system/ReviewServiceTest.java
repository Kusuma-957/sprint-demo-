//package com.hotelManagement.system;
//
//import com.hotelManagement.system.dto.ReviewCreateRequest;
//import com.hotelManagement.system.dto.ReviewResponseDTO;
//import com.hotelManagement.system.dto.ReviewUpdateRequest;
//import com.hotelManagement.system.entity.Reservation;
//import com.hotelManagement.system.entity.Review;
//import com.hotelManagement.system.exception.ConflictException;
//import com.hotelManagement.system.exception.EmptyListException;
//import com.hotelManagement.system.exception.ResourceNotFoundException;
//import com.hotelManagement.system.repository.ReviewRepository;
//import com.hotelManagement.system.service.ReviewService;
//
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//
//import org.springframework.test.util.ReflectionTestUtils; // <-- IMPORTANT: inject @PersistenceContext field
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReviewServiceTest {
//
//    @Mock private ReviewRepository reviewRepository;
//    @Mock private EntityManager entityManager;
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    private ReviewCreateRequest createReq;
//    private ReviewUpdateRequest updateReq;
//
//    private final LocalDate D = LocalDate.of(2026, 2, 20);
//
//    @BeforeEach
//    void setUp() {
//        // Inject the mocked EntityManager into the @PersistenceContext field of ReviewService
//        ReflectionTestUtils.setField(reviewService, "entityManager", entityManager);
//
//        createReq = new ReviewCreateRequest();
//        createReq.setReservationId(11);
//        createReq.setRating(5);
//        createReq.setComment("   Great Stay!   "); // to verify trim
//        createReq.setReviewDate(D);
//
//        updateReq = new ReviewUpdateRequest();
//        updateReq.setRating(4);
//        updateReq.setComment("Updated comment");
//        updateReq.setReviewDate(LocalDate.of(2026, 2, 21));
//    }
//
//    // --- helpers ---
//
//    private static Reservation reservation(Integer id) {
//        Reservation r = new Reservation();
//        r.setReservationId(id);
//        return r;
//    }
//
//    private static Review review(Integer id, Integer reservationId, Integer rating, String comment, LocalDate date) {
//        Review r = new Review();
//        r.setReviewId(id);
//        r.setReservation(reservation(reservationId));
//        r.setRating(rating);
//        r.setComment(comment);
//        r.setReviewDate(date);
//        return r;
//    }
//
//    // -------------------- create --------------------
//
//    @Test
//    @DisplayName("create: duplicate (same reservationId + normalized comment + rating + date) -> ConflictException")
//    void create_duplicate_throwsConflict() {
//        final String trimmed = "Great Stay!";
//        final String normalizedLower = trimmed.toLowerCase(Locale.ROOT);
//
//        when(reviewRepository.existsDuplicate(11, normalizedLower, 5, D)).thenReturn(true);
//
//        assertThatThrownBy(() -> reviewService.create(createReq))
//                .isInstanceOf(ConflictException.class)
//                .hasMessageContaining("Review already exist");
//
//        verify(reviewRepository).existsDuplicate(11, normalizedLower, 5, D);
//        verifyNoInteractions(entityManager);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("create: happy path -> getReference, map, persist trimmed comment, save, return DTO")
//    void create_ok_savesAndReturns() {
//        final String trimmed = "Great Stay!";
//        final String normalizedLower = trimmed.toLowerCase(Locale.ROOT);
//
//        when(reviewRepository.existsDuplicate(11, normalizedLower, 5, D)).thenReturn(false);
//
//        Reservation ref = reservation(11);
//        when(entityManager.getReference(Reservation.class, 11)).thenReturn(ref);
//
//        // capture entity passed to save
//        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
//
//        Review saved = review(101, 11, 5, trimmed, D);
//        when(reviewRepository.save(any(Review.class))).thenReturn(saved);
//
//        ReviewResponseDTO dto = reviewService.create(createReq);
//
//        assertThat(dto).isNotNull();
//        assertThat(dto.getReviewId()).isEqualTo(101);
//        assertThat(dto.getReservationId()).isEqualTo(11);
//        assertThat(dto.getRating()).isEqualTo(5);
//        assertThat(dto.getComment()).isEqualTo("Great Stay!");
//        assertThat(dto.getReviewDate()).isEqualTo(D);
//
//        verify(reviewRepository).existsDuplicate(11, normalizedLower, 5, D);
//        verify(entityManager).getReference(Reservation.class, 11);
//        verify(reviewRepository).save(captor.capture());
//
//        Review toPersist = captor.getValue();
//        assertThat(toPersist.getReservation().getReservationId()).isEqualTo(11);
//        // Service sets trimmed comment **after** mapping → must be trimmed
//        assertThat(toPersist.getComment()).isEqualTo("Great Stay!");
//    }
//
//    // -------------------- getAll --------------------
//
//    @Test
//    @DisplayName("getAll: empty -> EmptyListException")
//    void getAll_empty_throws() {
//        when(reviewRepository.findAll()).thenReturn(List.of());
//
//        assertThatThrownBy(() -> reviewService.getAll())
//                .isInstanceOf(EmptyListException.class)
//                .hasMessageContaining("Review list is empty");
//
//        verify(reviewRepository).findAll();
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("getAll: non-empty -> mapped list")
//    void getAll_nonEmpty_returnsList() {
//        when(reviewRepository.findAll()).thenReturn(List.of(
//                review(1, 1001, 4, "Nice", D),
//                review(2, 1002, 5, "Awesome", D.plusDays(1))
//        ));
//
//        List<ReviewResponseDTO> out = reviewService.getAll();
//
//        assertThat(out).hasSize(2);
//        assertThat(out.get(0).getReviewId()).isEqualTo(1);
//        assertThat(out.get(0).getReservationId()).isEqualTo(1001);
//        assertThat(out.get(1).getReviewId()).isEqualTo(2);
//        assertThat(out.get(1).getReservationId()).isEqualTo(1002);
//
//        verify(reviewRepository).findAll();
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    // -------------------- getById --------------------
//
//    @Test
//    @DisplayName("getById: missing -> ResourceNotFoundException")
//    void getById_missing_throws() {
//        when(reviewRepository.findById(77)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> reviewService.getById(77))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Review doesn't exist");
//
//        verify(reviewRepository).findById(77);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("getById: found -> mapped DTO")
//    void getById_found_returns() {
//        Review r = review(7, 55, 3, "ok", D);
//        when(reviewRepository.findById(7)).thenReturn(Optional.of(r));
//
//        ReviewResponseDTO dto = reviewService.getById(7);
//
//        assertThat(dto.getReviewId()).isEqualTo(7);
//        assertThat(dto.getReservationId()).isEqualTo(55);
//        assertThat(dto.getRating()).isEqualTo(3);
//        assertThat(dto.getComment()).isEqualTo("ok");
//        assertThat(dto.getReviewDate()).isEqualTo(D);
//
//        verify(reviewRepository).findById(7);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    // -------------------- getByRating --------------------
//
//    @Test
//    @DisplayName("getByRating: empty -> EmptyListException")
//    void getByRating_empty_throws() {
//        when(reviewRepository.findByRating(5)).thenReturn(List.of());
//
//        assertThatThrownBy(() -> reviewService.getByRating(5))
//                .isInstanceOf(EmptyListException.class)
//                .hasMessageContaining("Review list is empty");
//
//        verify(reviewRepository).findByRating(5);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("getByRating: non-empty -> mapped list")
//    void getByRating_ok_returnsList() {
//        when(reviewRepository.findByRating(4)).thenReturn(List.of(
//                review(9, 200, 4, "fine", D)
//        ));
//
//        List<ReviewResponseDTO> out = reviewService.getByRating(4);
//
//        assertThat(out).hasSize(1);
//        assertThat(out.get(0).getReviewId()).isEqualTo(9);
//        assertThat(out.get(0).getReservationId()).isEqualTo(200);
//        assertThat(out.get(0).getRating()).isEqualTo(4);
//
//        verify(reviewRepository).findByRating(4);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    // -------------------- getRecent --------------------
//
//    @Test
//    @DisplayName("getRecent: empty -> ResourceNotFoundException")
//    void getRecent_empty_throws() {
//        when(reviewRepository.findTop5ByOrderByReviewDateDesc()).thenReturn(List.of());
//
//        assertThatThrownBy(() -> reviewService.getRecent())
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Review doesn't exist");
//
//        verify(reviewRepository).findTop5ByOrderByReviewDateDesc();
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("getRecent: non-empty -> mapped list")
//    void getRecent_nonEmpty_returnsList() {
//        when(reviewRepository.findTop5ByOrderByReviewDateDesc()).thenReturn(List.of(
//                review(31, 310, 5, "great", D.plusDays(2)),
//                review(32, 311, 4, "good", D.plusDays(1))
//        ));
//
//        List<ReviewResponseDTO> out = reviewService.getRecent();
//
//        assertThat(out).hasSize(2);
//        assertThat(out.get(0).getReviewId()).isEqualTo(31);
//        assertThat(out.get(1).getReviewId()).isEqualTo(32);
//
//        verify(reviewRepository).findTop5ByOrderByReviewDateDesc();
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    // -------------------- list(Pageable) --------------------
//
//    @Test
//    @DisplayName("list(Pageable): returns mapped Page<ReviewResponseDTO>")
//    void list_paged_returnsMappedPage() {
//        Pageable pageable = PageRequest.of(0, 2, Sort.by("reviewId").ascending());
//
//        List<Review> content = List.of(
//                review(100, 1000, 5, "A", D),
//                review(101, 1001, 4, "B", D.plusDays(1))
//        );
//        Page<Review> page = new PageImpl<>(content, pageable, 2);
//
//        when(reviewRepository.findAll(pageable)).thenReturn(page);
//
//        Page<ReviewResponseDTO> result = reviewService.list(pageable);
//
//        assertThat(result.getTotalElements()).isEqualTo(2);
//        assertThat(result.getContent()).hasSize(2);
//        assertThat(result.getContent().get(0).getReviewId()).isEqualTo(100);
//        assertThat(result.getContent().get(1).getReviewId()).isEqualTo(101);
//
//        verify(reviewRepository).findAll(pageable);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    // -------------------- update --------------------
//
//    @Test
//    @DisplayName("update: missing -> ResourceNotFoundException")
//    void update_missing_throws() {
//        when(reviewRepository.findById(404)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> reviewService.update(404, updateReq))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Review doesn't exist");
//
//        verify(reviewRepository).findById(404);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("update: happy path (dirty checking) -> fields changed & DTO returned")
//    void update_ok_dirtyChecking() {
//        Review existing = review(55, 500, 3, "old", D);
//        when(reviewRepository.findById(55)).thenReturn(Optional.of(existing));
//
//        ReviewResponseDTO dto = reviewService.update(55, updateReq);
//
//        assertThat(existing.getRating()).isEqualTo(4);
//        assertThat(existing.getComment()).isEqualTo("Updated comment");
//        assertThat(existing.getReviewDate()).isEqualTo(LocalDate.of(2026, 2, 21));
//
//        assertThat(dto.getReviewId()).isEqualTo(55);
//        assertThat(dto.getRating()).isEqualTo(4);
//        assertThat(dto.getComment()).isEqualTo("Updated comment");
//        assertThat(dto.getReviewDate()).isEqualTo(LocalDate.of(2026, 2, 21));
//
//        verify(reviewRepository).findById(55);
//        // no save in service (JPA dirty checking)
//        verifyNoMoreInteractions(reviewRepository);
//        verifyNoInteractions(entityManager);
//    }
//
//    // -------------------- delete --------------------
//
//    @Test
//    @DisplayName("delete: missing -> ResourceNotFoundException")
//    void delete_missing_throws() {
//        when(reviewRepository.existsById(77)).thenReturn(false);
//
//        assertThatThrownBy(() -> reviewService.delete(77))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessageContaining("Review doesn't exist");
//
//        verify(reviewRepository).existsById(77);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//
//    @Test
//    @DisplayName("delete: exists -> deleteById")
//    void delete_ok() {
//        when(reviewRepository.existsById(78)).thenReturn(true);
//
//        reviewService.delete(78);
//
//        verify(reviewRepository).existsById(78);
//        verify(reviewRepository).deleteById(78);
//        verifyNoMoreInteractions(reviewRepository);
//    }
//}

package com.hotelManagement.system;

import com.hotelManagement.system.dto.ReviewCreateRequest;
import com.hotelManagement.system.dto.ReviewResponseDTO;
import com.hotelManagement.system.dto.ReviewUpdateRequest;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Review;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.repository.ReviewRepository;
import com.hotelManagement.system.service.ReviewService;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private EntityManager entityManager;

    @InjectMocks
    private ReviewService reviewService;

    private ReviewCreateRequest createReq;
    private ReviewUpdateRequest updateReq;

    private final LocalDate D = LocalDate.of(2026, 2, 20);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reviewService, "entityManager", entityManager);

        createReq = new ReviewCreateRequest();
        createReq.setReservationId(11);
        createReq.setRating(5);
        createReq.setComment("   Great Stay!   ");
        createReq.setReviewDate(D);

        updateReq = new ReviewUpdateRequest();
        updateReq.setRating(4);
        updateReq.setComment("Updated comment");
        updateReq.setReviewDate(LocalDate.of(2026, 2, 21));
    }


    // ---------------- Helpers -----------------
    private static Reservation reservation(Integer id) {
        Reservation r = new Reservation();
        r.setReservationId(id);
        return r;
    }

    private static Review review(Integer id, Integer reservationId, Integer rating,
                                 String comment, LocalDate date) {
        Review r = new Review();
        r.setReviewId(id);
        r.setReservation(reservation(reservationId));
        r.setRating(rating);
        r.setComment(comment);
        r.setReviewDate(date);
        return r;
    }


    // ---------------- create -----------------

    @Test
    @DisplayName("create: duplicate → ConflictException")
    void create_duplicate_throwsConflict() {
        final String trimmed = "Great Stay!";
        final String lower = trimmed.toLowerCase(Locale.ROOT);

        when(reviewRepository.existsDuplicate(11, lower, 5, D)).thenReturn(true);

        ConflictException ex = assertThrows(ConflictException.class,
                () -> reviewService.create(createReq));

        assertTrue(ex.getMessage().contains("Review already exist"));

        verify(reviewRepository).existsDuplicate(11, lower, 5, D);
        verifyNoInteractions(entityManager);
    }

    @Test
    @DisplayName("create: happy path → save & map DTO")
    void create_ok_savesAndReturns() {
        final String trimmed = "Great Stay!";
        final String lower = trimmed.toLowerCase(Locale.ROOT);

        when(reviewRepository.existsDuplicate(11, lower, 5, D)).thenReturn(false);

        Reservation ref = reservation(11);
        when(entityManager.getReference(Reservation.class, 11)).thenReturn(ref);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        Review saved = review(101, 11, 5, trimmed, D);
        when(reviewRepository.save(any(Review.class))).thenReturn(saved);

        ReviewResponseDTO dto = reviewService.create(createReq);

        assertNotNull(dto);
        assertEquals(101, dto.getReviewId());
        assertEquals(11, dto.getReservationId());
        assertEquals(5, dto.getRating());
        assertEquals("Great Stay!", dto.getComment());
        assertEquals(D, dto.getReviewDate());

        verify(reviewRepository).save(captor.capture());
        Review persisted = captor.getValue();
        assertEquals("Great Stay!", persisted.getComment());
    }


    // ---------------- getAll -----------------

    @Test
    @DisplayName("getAll: empty → EmptyListException")
    void getAll_empty_throws() {
        when(reviewRepository.findAll()).thenReturn(List.of());

        assertThrows(EmptyListException.class,
                () -> reviewService.getAll());
    }

    @Test
    @DisplayName("getAll: non-empty → list mapped")
    void getAll_nonEmpty_returnsList() {
        when(reviewRepository.findAll()).thenReturn(List.of(
                review(1, 1001, 4, "Nice", D),
                review(2, 1002, 5, "Awesome", D.plusDays(1))
        ));

        List<ReviewResponseDTO> list = reviewService.getAll();

        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getReviewId());
        assertEquals(2, list.get(1).getReviewId());
    }


    // ---------------- getById -----------------

    @Test
    @DisplayName("getById: missing → ResourceNotFoundException")
    void getById_missing() {
        when(reviewRepository.findById(88)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getById(88));
    }

    @Test
    @DisplayName("getById: found → DTO")
    void getById_found() {
        Review r = review(9, 200, 3, "ok", D);
        when(reviewRepository.findById(9)).thenReturn(Optional.of(r));

        ReviewResponseDTO dto = reviewService.getById(9);

        assertEquals(9, dto.getReviewId());
        assertEquals(200, dto.getReservationId());
        assertEquals(3, dto.getRating());
        assertEquals("ok", dto.getComment());
    }


    // ---------------- getByRating -----------------

    @Test
    @DisplayName("getByRating: empty → EmptyListException")
    void getByRating_empty() {
        when(reviewRepository.findByRating(5)).thenReturn(List.of());

        assertThrows(EmptyListException.class,
                () -> reviewService.getByRating(5));
    }

    @Test
    @DisplayName("getByRating: non-empty → list")
    void getByRating_ok() {
        when(reviewRepository.findByRating(4)).thenReturn(List.of(
                review(50, 900, 4, "fine", D)
        ));

        List<ReviewResponseDTO> list = reviewService.getByRating(4);

        assertEquals(1, list.size());
        assertEquals(50, list.get(0).getReviewId());
    }


    // ---------------- getRecent -----------------

    @Test
    @DisplayName("getRecent: empty → ResourceNotFoundException")
    void getRecent_empty() {
        when(reviewRepository.findTop5ByOrderByReviewDateDesc()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getRecent());
    }

    @Test
    @DisplayName("getRecent: non-empty → list")
    void getRecent_nonEmpty() {
        when(reviewRepository.findTop5ByOrderByReviewDateDesc()).thenReturn(List.of(
                review(31, 310, 5, "great", D.plusDays(2)),
                review(32, 311, 4, "good", D.plusDays(1))
        ));

        List<ReviewResponseDTO> list = reviewService.getRecent();

        assertEquals(2, list.size());
        assertEquals(31, list.get(0).getReviewId());
    }


    // ---------------- update -----------------

    @Test
    @DisplayName("update: missing → ResourceNotFoundException")
    void update_missing() {
        when(reviewRepository.findById(404)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.update(404, updateReq));
    }

    @Test
    @DisplayName("update: ok → dirty checking + DTO")
    void update_ok() {
        Review existing = review(55, 500, 3, "old", D);
        when(reviewRepository.findById(55)).thenReturn(Optional.of(existing));

        ReviewResponseDTO dto = reviewService.update(55, updateReq);

        assertEquals(4, existing.getRating());
        assertEquals("Updated comment", existing.getComment());
        assertEquals(LocalDate.of(2026, 2, 21), existing.getReviewDate());

        assertEquals(55, dto.getReviewId());
    }


    // ---------------- delete -----------------

    @Test
    @DisplayName("delete: missing → ResourceNotFoundException")
    void delete_missing() {
        when(reviewRepository.existsById(77)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.delete(77));
    }

    @Test
    @DisplayName("delete: ok")
    void delete_ok() {
        when(reviewRepository.existsById(78)).thenReturn(true);

        reviewService.delete(78);

        verify(reviewRepository).existsById(78);
        verify(reviewRepository).deleteById(78);
    }
}