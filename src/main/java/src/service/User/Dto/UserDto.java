
package src.service.User.Dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import src.service.Role.Dto.RoleDto;

import java.util.Date;
import java.util.UUID;

@Data
public class UserDto extends UserUpdateDto {
    @JsonProperty(value = "Id", required = true)
    public int Id;
    @JsonProperty(value = "isDeleted")
    public Boolean isDeleted  = false;
    @JsonProperty(value = "createAt", required = true)
    public Date createAt ;
    @JsonProperty(value = "updateAt", required = true)
    public Date updateAt ;
    @JsonProperty(value = "roleId", required = true)
    public int roleId;

}

