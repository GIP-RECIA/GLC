package fr.recia.glc.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggingAuditSink implements AuditSink {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private final ObjectMapper objectMapper;

    @Override
    public void write(AuditEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            auditLogger.info(json);
        } catch (JsonProcessingException e) {
            auditLogger.error("Failed to serialize audit event", e);
        }
    }

}
