package com.razdeep.konsignapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({"billNo"})
public record Bill(
        @JsonProperty("supplierName") @NotBlank String supplierName,

        @JsonProperty("buyerName") @NotBlank String buyerName,

        @JsonProperty("billNo") @NotBlank String billNo,

        @JsonProperty("billDate") @NotNull LocalDate billDate,

        @JsonProperty("transportName") @NotBlank String transportName,

        @JsonProperty("lrDate") @NotNull LocalDate lrDate,

        @JsonProperty("lrPmList") @NotNull List<LrPm> lrPmList,

        @JsonProperty("billAmount") @NotNull @Positive BigDecimal billAmount) {}
