package src.service.User.auth;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String email;
    private String oldPassword;
    private String newPassword;

}
