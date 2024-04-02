
package src.service.User.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserUpdateDto extends  UserCreateDto{
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;

}

