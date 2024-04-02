package src.config.dto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PaginationSerializer extends JsonSerializer<Pagination> {
    @Override
    public void serialize(Pagination pagination, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("currentPage", pagination.getSkip());
        jsonGenerator.writeNumberField("itemPerPage", pagination.getLimit());
        jsonGenerator.writeNumberField("pageCounts", pagination.getTotal());
        jsonGenerator.writeEndObject();
    }
}
