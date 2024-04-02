package src.service.User.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class UserCreateDto {
    @JsonProperty(value = "fullname", required = true)
    private String fullname;
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "phone", required = true, defaultValue = "0")
    private String phone;
    @JsonProperty(value = "avatar", required = true, defaultValue = "0")
    private String avatar;
    @JsonProperty(value = "description", required = true)
    private String description;
    @JsonProperty(value = "bank_name", required = true, defaultValue = "0")
    private String bank_name;
    @JsonProperty(value = "account_number", required = true, defaultValue = "0")
    private String account_number;
    @JsonProperty(value = "account_name", required = true, defaultValue = "0")
    private String account_name;
    @JsonProperty(value = "password", required = true, defaultValue = "0")
    private String password;
    @JsonProperty(value = "roleId")
    private int roleId;
}