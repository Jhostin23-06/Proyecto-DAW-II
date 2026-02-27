package com.transporte.mstransportistas.domain.bean;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transportUserId;
    private String transportType;
    @Positive(message = "La capacidad del transporte debe ser positivo y mayor a 0")
    private Double transportCapacity;
    private String transportLocation;
    private String transportDriver;
    private String transportLicensePlate;
    private String transportCompany;


}
