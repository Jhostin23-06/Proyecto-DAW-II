package com.transporte.mstransportistas.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long transportUserId;
    private String transportType;
    private Double transportCapacity;
    private String transportLocation;
    private String transportDriver;
    private String transportLicensePlate;
    private String transportCompany;


}
