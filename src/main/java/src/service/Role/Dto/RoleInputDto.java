package src.service.Role.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RoleInputDto {
    @JsonProperty(value = "name", required = true)
    private String name;
}
