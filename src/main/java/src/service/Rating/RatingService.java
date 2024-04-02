package src.service.Rating;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import src.Dto.RatingDTO;
import src.config.exception.NotFoundException;
import src.model.Rating;
import src.repository.RatingRepository;
import src.service.Rating.Dto.RatingCreateDto;
import src.service.Rating.Dto.RatingDto;
import src.service.Rating.Dto.RatingUpdateDto;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private ModelMapper toDto;
    @Async
    public CompletableFuture<List<RatingDto>> getAll() {
        return CompletableFuture.completedFuture(
                ratingRepository.findAll().stream().map(
                        x -> toDto.map(x, RatingDto.class)
                ).collect(Collectors.toList()));
    }
    @Async
    public CompletableFuture<RatingDto> getOne(int id) {
        Rating rating = ratingRepository.findById(id).orElse(null);
        if (rating == null) {
            throw new NotFoundException("Không tìm thấy quyền với ID " + id);
        }
        return CompletableFuture.completedFuture(toDto.map(rating, RatingDto.class));
    }
    @Async
    public CompletableFuture<RatingDto> create(RatingCreateDto input) {
        Rating rating = new Rating();
        rating.setRating(input.getRating());
        rating.setCourseId(input.getCourseId());
        rating.setUserId(input.getUserId());
        Rating savedRating = ratingRepository.save(rating);
        return CompletableFuture.completedFuture(toDto.map(savedRating, RatingDto.class));
    }
    @Async
    public CompletableFuture<RatingDto> update(int id, RatingUpdateDto ratings) {
        Rating existingRating = ratingRepository.findById(id).orElse(null);
        if (existingRating == null)
            throw new NotFoundException("Unable to find rating!");
        BeanUtils.copyProperties(ratings, existingRating);
        existingRating.setUpdateAt(new Date(new java.util.Date().getTime()));
        return CompletableFuture.completedFuture(toDto.map(ratingRepository.save(existingRating), RatingDto.class));
    }
    @Async
    public CompletableFuture<String> deleteById(int id) {
        Optional<Rating> ratingOptional = ratingRepository.findById(id);
        if (!ratingOptional.isPresent()) {
            return CompletableFuture.completedFuture("Không có ID này");
        }
        try {
            Rating rating = ratingOptional.get();
            rating.setIsDeleted(true);
            rating.setUpdateAt(new Date(new java.util.Date().getTime()));
            ratingRepository.save(rating);
            return CompletableFuture.completedFuture("Đánh dấu xóa thành công");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Xóa không được");
        }
    }
    public Rating updateRating(int ratingId, Map<String, Object> fieldsToUpdate) {
        Optional<Rating> optionalRating = ratingRepository.findById(ratingId);
        if (optionalRating.isPresent()) {
            Rating rating = optionalRating.get();
            updateRatingFields(rating, fieldsToUpdate);
            rating.setUpdateAt(new Date());
            ratingRepository.save(rating);
            return rating;
        }
        return null;
    }
    private void updateRatingFields(Rating rating, Map<String, Object> fieldsToUpdate) {
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            updateRatingField(rating, fieldName, value);
        }
    }
    private void updateRatingField(Rating rating, String fieldName, Object value) {
        switch (fieldName) {
            case "rating":
                rating.setRating((int) value);
                break;
            case "courseId":
                rating.setCourseId((int) value);
                break;
            case "userId":
                rating.setUserId((int) value);
                break;
            default:
                break;
        }
    }
    public Double calculateOverallRating(int courseId) {
        return ratingRepository.calculateOverallRating(courseId);
    }
    public Map<Integer, Double> calculateRatingDistribution(int courseId) {
        List<Object[]> distributionData = ratingRepository.calculateRatingDistribution(courseId);
        Map<Integer, Double> distributionMap = new HashMap<>();
        long totalRatings = getTotalRatings(distributionData);
        for (int i = 1; i <= 5; i++) {
            distributionMap.put(i, 0.0);
        }
        for (Object[] data : distributionData) {
            int rating = (int) data[0];
            long count = (long) data[1];
            double percentage = (totalRatings > 0) ? (count / (double) totalRatings) * 100.0 : 0.0;
            distributionMap.put(rating, percentage);
        }
        return distributionMap;
    }
    private double getPercentage(List<Object[]> distributionData, int rating, long totalRatings) {
        for (Object[] data : distributionData) {
            int currentRating = (int) data[0];
            long count = (long) data[1];
            if (currentRating == rating) {
                return (count / totalRatings) * 100.0;
            }
        }
        return 0.0;
    }
    private long getTotalRatings(List<Object[]> distributionData) {
        return distributionData.stream().mapToLong(data -> (long) data[1]).sum();
    }
    public Long countStudentsForCourse(int courseId) {
        return ratingRepository.countDistinctUsersByCourseId(courseId);
    }
    public List<RatingDTO> findRatingByUserCourse(int userId, int courseId) {
        List<RatingDTO> result = new ArrayList<>();
        List<Rating> ratings = ratingRepository.findRatingByUserCourse(userId, courseId);
        for (Rating rating : ratings) {
            RatingDTO dto = new RatingDTO();
            dto.setId(rating.getId());
            dto.setUserId(rating.getUserByUserId().getId());
            dto.setCourseId(rating.getCourseId());
            dto.setRating(rating.getRating());
            result.add(dto);
        }
        return result;
    }

    public int getTotalRatingsByUserId(int userId) {
        return ratingRepository.countTotalRatingByUserId(userId);
    }
}
