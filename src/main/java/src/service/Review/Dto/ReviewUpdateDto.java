package src.service.Review.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReviewUpdateDto extends ReviewCreateDto {
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
}
