package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.ReviewCreateRequest;
import com.hotelManagement.system.dto.ReviewResponseDTO;
import com.hotelManagement.system.dto.ReviewUpdateRequest;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Review;
import com.hotelManagement.system.mapper.ReviewMapper;
import com.hotelManagement.system.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewResponseDTO create(ReviewCreateRequest request) {
        // Use a reference so we don't load the full Reservation entity
        Reservation reservationRef = entityManager.getReference(Reservation.class, request.getReservationId());
        Review entity = ReviewMapper.toEntity(request, reservationRef);
        Review saved = reviewRepository.save(entity);
        return ReviewMapper.toDto(saved);
    }

    public ReviewResponseDTO getById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not found: " + reviewId));
        return ReviewMapper.toDto(review);
    }

    public Page<ReviewResponseDTO> list(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewMapper::toDto);
    }

    public Page<ReviewResponseDTO> listByReservation(Integer reservationId, Pageable pageable) {
        return reviewRepository.findByReservationId(reservationId, pageable).map(ReviewMapper::toDto);
    }

    public ReviewResponseDTO update(Integer reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not found: " + reviewId));
        ReviewMapper.updateEntity(request, review);
        // JPA dirty checking persists changes on commit
        return ReviewMapper.toDto(review);
    }

    public void delete(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}