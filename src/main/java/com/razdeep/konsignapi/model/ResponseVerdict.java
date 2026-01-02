package com.razdeep.konsignapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVerdict implements Serializable {

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Object data;
}
