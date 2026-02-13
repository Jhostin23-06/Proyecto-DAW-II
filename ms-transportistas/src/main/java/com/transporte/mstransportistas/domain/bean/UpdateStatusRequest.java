package com.transporte.mstransportistas.domain.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String transportStatus; // String para recibir del request
    private String location;
    private String reason;

}
