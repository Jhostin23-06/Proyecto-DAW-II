package com.core.msauditoria.application.port.usecase;

import com.core.msauditoria.domain.bean.AuditEventRecordRequest;

public interface RecordAuditEventPort {
    void record(AuditEventRecordRequest request);
}
