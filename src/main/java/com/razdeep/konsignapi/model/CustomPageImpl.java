package com.razdeep.konsignapi.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CustomPageImpl<T> {

    int totalPages, number, size, numberOfElements;

    long totalElements;

    List<T> content;

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public List<T> getContent() {
        return content;
    }

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
