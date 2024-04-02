package src.service.User.auth;

import lombok.Data;

@Data
public class ConfirmResetDto {
    private String confirmationCode;
    private String newPassword;
}
