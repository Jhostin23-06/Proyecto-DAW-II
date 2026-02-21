package core.cibertec.ms_servicios.domain.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long statusId;
    private String statusName;
}
