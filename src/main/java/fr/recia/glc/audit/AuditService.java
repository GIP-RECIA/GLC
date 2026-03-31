package fr.recia.glc.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final List<AuditSink> auditSinks;

    public void log(AuditEvent event) {
        for (AuditSink sink : auditSinks) {
            sink.write(event);
        }
    }
}
