package src.config.dto;

import java.util.List;

public class SuccessResponsePagedDto <TDto>{
    private Boolean success;
    private Pagination pagination;
    private List<TDto> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<TDto> getData() {
        return data;
    }

    public void setData(List<TDto> data) {
        this.data = data;
    }
    public SuccessResponsePagedDto() {
    }
    public SuccessResponsePagedDto(Pagination pagination, List<TDto> data) {
        this.data = data;
        this.pagination = pagination;
        this.success = true;
    }
    public static <TDto>SuccessResponsePagedDto<TDto> create(Pagination pagination, List<TDto> data) {
        return new SuccessResponsePagedDto(pagination, data);
    }
}
