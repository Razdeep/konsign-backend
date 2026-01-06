package com.razdeep.konsignapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class CustomPageImpl<T> {

    int totalPages, number, size, numberOfElements;

    long totalElements;

    List<T> content;

    public boolean hasContent() {
        return content != null && !content.isEmpty();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CustomPageImpl(
            @JsonProperty("content") List<T> content,
            @JsonProperty("number") int page,
            @JsonProperty("size") int size,
            @JsonProperty("totalElements") long total) {
        this.content = content;
        this.number = page;
        this.size = size;
        this.totalElements = total;
    }
}
