package src.service.User.auth;

import lombok.Data;

@Data
public class ForgetPasswordDto {
    private String email;
    private String newPassword;
    private String rePassword;


}
