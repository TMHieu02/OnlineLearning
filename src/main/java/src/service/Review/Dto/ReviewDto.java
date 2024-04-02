package src.service.Review.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ReviewDto extends ReviewUpdateDto {
    @JsonProperty(value = "Id", required = true)
    public int Id;
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
    @JsonProperty(value = "createAt", required = true)
    public Date createAt ;
    @JsonProperty(value = "updateAt", required = true)
    public Date updateAt ;
}
