package com.hotelManagement.system.service;

import com.hotelManagement.system.dto.ReviewCreateRequest;
import com.hotelManagement.system.dto.ReviewResponseDTO;
import com.hotelManagement.system.dto.ReviewUpdateRequest;
import com.hotelManagement.system.entity.Reservation;
import com.hotelManagement.system.entity.Review;
import com.hotelManagement.system.exception.ConflictException;
import com.hotelManagement.system.exception.EmptyListException;
import com.hotelManagement.system.exception.ResourceNotFoundException;
import com.hotelManagement.system.mapper.ReviewMapper;
import com.hotelManagement.system.repository.ReviewRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /**
     * POST /api/review/post
     * Success handled in controller as {code: POSTSUCCESS, message: ...}
     * Duplicate => throw ConflictException("Review already exist") -> maps to {code: ADDFAILS, ...}
     */
    public ReviewResponseDTO create(ReviewCreateRequest request) {
        // Normalize comment: trim + toLowerCase for dup check; persist trimmed form
        final String trimmed = request.getComment() == null ? null : request.getComment().trim();
        final String normalizedLower = trimmed == null ? null : trimmed.toLowerCase(Locale.ROOT);

        boolean duplicate = reviewRepository.existsDuplicate(
                request.getReservationId(),
                normalizedLower,
                request.getRating(),
                request.getReviewDate()
        );
        if (duplicate) {
            // -> Global handler returns code ADDFAILS (409)
            throw new ConflictException("Review already exist");
        }

        Reservation reservationRef =
                entityManager.getReference(Reservation.class, request.getReservationId());

        Review entity = ReviewMapper.toEntity(request, reservationRef);
        entity.setComment(trimmed); // persist normalized comment

        Review saved = reviewRepository.save(entity);
        return ReviewMapper.toDto(saved);
    }

    /**
     * GET /api/review/all
     * Empty => EmptyListException("Review list is empty") -> {code: GETALLFAILS}
     */
    public List<ReviewResponseDTO> getAll() {
        List<Review> reviews = reviewRepository.findAll();
        if (reviews.isEmpty()) {
            throw new EmptyListException("Review list is empty");
        }
        return reviews.stream().map(ReviewMapper::toDto).toList();
    }

    /**
     * GET /api/review/{review_id}
     * Not found => ResourceNotFoundException("Review doesn't exist") -> {code: GETFAILS}
     */
    public ReviewResponseDTO getById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review doesn't exist"));
        return ReviewMapper.toDto(review);
    }

    /**
     * GET /api/reviews/rating/{rating}
     * Empty => EmptyListException("Review list is empty") -> {code: GETALLFAILS}
     */
    public List<ReviewResponseDTO> getByRating(Integer rating) {
        List<Review> reviews = reviewRepository.findByRating(rating);
        if (reviews.isEmpty()) {
            throw new EmptyListException("Review list is empty");
        }
        return reviews.stream().map(ReviewMapper::toDto).toList();
    }

    /**
     * GET /api/reviews/recent
     * If none => ResourceNotFoundException("Review doesn't exist") -> {code: GETFAILS}
     */
    public List<ReviewResponseDTO> getRecent() {
        List<Review> reviews = reviewRepository.findTop5ByOrderByReviewDateDesc();
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("Review doesn't exist");
        }
        return reviews.stream().map(ReviewMapper::toDto).toList();
    }

    /**
     * GET (paged) if you still need pagination elsewhere.
     */
    public Page<ReviewResponseDTO> list(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewMapper::toDto);
    }

    /**
     * PUT /api/review/update/{review_id}
     * Not found => ResourceNotFoundException("Review doesn't exist") -> {code: UPDTFAILS}
     */
    public ReviewResponseDTO update(Integer reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review doesn't exist"));

        // If you want to prevent duplicates on update when comment/rating/date change,
        // we can add the same duplicate check here — tell me and I'll patch it.
        ReviewMapper.updateEntity(request, review);
        return ReviewMapper.toDto(review); // dirty checking will persist
    }

    /**
     * DELETE /api/review/delete/{review_id}
     * Not found => ResourceNotFoundException("Review doesn't exist") -> {code: DLTFAILS}
     */
    public void delete(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review doesn't exist");
        }
        reviewRepository.deleteById(reviewId);
    }
}