package com.razdeep.konsignapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.razdeep.konsignapi.entity.BillEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"billNo"})
public class Bill {

    @JsonProperty("supplierName")
    private String supplierName;

    @JsonProperty("buyerName")
    private String buyerName;

    @JsonProperty("billNo")
    private String billNo;

    @JsonProperty("billDate")
    private LocalDate billDate;

    @JsonProperty("transportName")
    private String transportName;

    @JsonProperty("lrDate")
    private LocalDate lrDate;

    @JsonProperty("lrPmList")
    private List<LrPm> lrPmList;

    @JsonProperty("billAmount")
    private Double billAmount;

    public Bill(BillEntity other) {
        this.supplierName = other.getSupplierEntity().getSupplierName();
        this.buyerName = other.getBuyerEntity().getBuyerName();
        this.billNo = other.getBillNo();
        this.billDate = other.getBillDate();
        this.transportName = other.getTransportEntity().getTransportName();
        this.lrDate = other.getLrDate();
        this.lrPmList = other.getLrPmEntityList().stream()
                .map(lrPmEntity -> new LrPm(lrPmEntity.getLr(), lrPmEntity.getPm()))
                .collect(Collectors.toList());
        this.billAmount = other.getBillAmount();
    }

    public boolean anyFieldEmpty() {
        return Objects.equals(supplierName, "")
                || Objects.equals(buyerName, "")
                || Objects.equals(billNo, "")
                || Objects.equals(billDate, "")
                || Objects.equals(transportName, "")
                || Objects.equals(lrDate, "")
                || billAmount == 0.f;
    }
}
