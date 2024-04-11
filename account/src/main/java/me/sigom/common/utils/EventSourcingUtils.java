package me.sigom.common.utils;

import me.sigom.domain.model.AggregateRoot;
import me.sigom.domain.model.Event;
import me.sigom.domain.model.Snapshot;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

import static me.sigom.common.utils.Constans.EVENTS;

public class EventSourcingUtils {
    private EventSourcingUtils() {
    }

    public static String getAggregateTypeTopic(final String aggregateType) {
        return String.format("%s_%s", aggregateType, EVENTS);
    }

    public static <T extends AggregateRoot> Snapshot snapshotFromAggregate(final T aggregate) {
        byte[] bytes = SerializerUtils.serializeToJsonBytes(aggregate);
        return Snapshot.builder()
                .snapshotId(UUID.randomUUID())
                .aggregateId(aggregate.getId())
                .aggregateType(aggregate.getType())
                .version(aggregate.getVersion())
                .data(bytes)
                .timestamp(ZonedDateTime.now())
                .build();
    }

    public static Snapshot snapshotFromEvent(final Event event) {
        return Snapshot.builder()
                .snapshotId(UUID.randomUUID())
                .aggregateId(event.getAggregateId())
                .aggregateType(event.getAggregateType())
                .version(event.getVersion())
                .data(event.getData())
                .timestamp(ZonedDateTime.now())
                .build();
    }
    public static <T extends AggregateRoot> T aggregateFromSnapshot(final Snapshot snapshot, final Class<T> valueType) {
        return SerializerUtils.deserializeFromJsonBytes(snapshot.getData(), valueType);
    }
}
