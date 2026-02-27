package com.core.msauditoria.application.service;

import com.core.msauditoria.application.port.outservice.AuditEventPersistencePort;
import com.core.msauditoria.application.port.usecase.RecordAuditEventPort;
import com.core.msauditoria.domain.bean.AuditEventRecordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordAuditEventServiceImpl implements RecordAuditEventPort {

    private final AuditEventPersistencePort persistencePort;

    @Override
    public void record(AuditEventRecordRequest request) {
        persistencePort.save(request);
        log.debug("Audit event persisted source={} eventType={} correlationId={}",
                request.eventSource(), request.eventType(), request.correlationId());
    }
}
