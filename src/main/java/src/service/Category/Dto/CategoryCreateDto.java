package src.service.Category.Dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryCreateDto {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "image", required = true)
    private String image;
    @JsonProperty(value = "parentCategoryId", required = true)
    private int parentCategoryId;
}