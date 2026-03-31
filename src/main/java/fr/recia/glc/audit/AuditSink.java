package fr.recia.glc.audit;

public interface AuditSink {
    void write(AuditEvent event);
}
