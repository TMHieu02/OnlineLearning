
package src.service.Category.Dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;

@Data
public class CategoryDto extends CategoryUpdateDto {
    @JsonProperty(value = "Id", required = true)
    public int Id;
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
    @JsonProperty(value = "createAt", required = true)
    public Date createAt ;
    @JsonProperty(value = "updateAt", required = true)
    public Date updateAt ;

}

