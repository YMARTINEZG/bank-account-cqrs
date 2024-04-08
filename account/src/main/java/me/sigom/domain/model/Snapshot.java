package me.sigom.domain.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Snapshot {
    @Id
    private UUID snapshotId;
    private String aggregateId;
    private String aggregateType;
    private byte[] data;
    private byte[] metadata;
    private long version;
    private ZonedDateTime timestamp;
    @Override
    public String toString() {
        return "Snapshot{" +
                "id=" + snapshotId +
                ", aggregateId='" + aggregateId + '\'' +
                ", aggregateType='" + aggregateType + '\'' +
                ", version=" + version +
                ", timeStamp=" + timestamp +
                '}';
    }
}
