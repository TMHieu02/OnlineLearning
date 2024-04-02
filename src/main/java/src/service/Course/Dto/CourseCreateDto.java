package src.service.Course.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CourseCreateDto {
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "price", required = true, defaultValue = "0")
    private double price;
    @JsonProperty(value = "promotional_price", required = true, defaultValue = "0")
    private double promotional_price;
    @JsonProperty(value = "sold", required = true, defaultValue = "0")
    private double sold;
    @JsonProperty(value = "description", required = true)
    private String description;
    @JsonProperty(value = "active", required = true)
    private Boolean active;
    @JsonProperty(value = "rating", required = true, defaultValue = "0")
    private float rating;
    @JsonProperty(value = "image", required = true)
    private String image;
    @JsonProperty(value = "categoryId", required = true)
    private int categoryId;
    @JsonProperty(value = "userId", required = true)
    private int userId;
}
