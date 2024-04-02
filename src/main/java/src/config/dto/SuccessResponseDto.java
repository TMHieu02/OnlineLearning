package src.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class SuccessResponseDto<T> {
    private Boolean success = true;
    private T data;

    public SuccessResponseDto(T data) {
        this.data = data;
    }

    @Schema(description = "Response data", required = true)
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Schema(description = "Response data")
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }


}
