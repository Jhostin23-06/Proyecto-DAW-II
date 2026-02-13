package com.transporte.mstransportistas.domain.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long transportId;
    private Long transportUserId;
    private String transportType;
    private Double transportCapacity;
    private String transportStatus;
    private String transportStatusDescription;
    private String transportLocation;
    private String transportDriver;
    private String transportLicensePlate;
    private String transportCompany;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private Boolean active;
    private Boolean available;

}
