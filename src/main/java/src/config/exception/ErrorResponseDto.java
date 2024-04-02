package src.config.exception;

public class ErrorResponseDto {
    private String errorMessage;

    public ErrorResponseDto(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
