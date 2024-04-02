package src.service.Rating.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RatingInputDto {
    @JsonProperty(value = "content", required = true)
    private String content;
    @JsonProperty(value = "rating", required = true)
    private int rating;
}
