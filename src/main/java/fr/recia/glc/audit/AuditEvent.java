package fr.recia.glc.audit;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
public class AuditEvent {

    private OffsetDateTime timestamp;
    private EventType eventType;
    private String actor;
    private String target;
    private Map<String, Object> payload;

}
