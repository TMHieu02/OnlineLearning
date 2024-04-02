package src.service.User.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginInputDto {
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty(value = "email", required = true)
    String email;
    @JsonProperty(value = "password", required = true)
    String password;
}
