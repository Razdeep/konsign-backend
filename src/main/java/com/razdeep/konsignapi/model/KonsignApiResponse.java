package com.razdeep.konsignapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KonsignApiResponse implements Serializable {

    @JsonProperty("success")
    private boolean success = true;

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private Object data;
}
