package me.sigom.adapter.out.persistence;


import me.sigom.adapter.out.broker.EventBus;
import me.sigom.common.utils.EventSourcingUtils;
import me.sigom.domain.model.AggregateRoot;
import me.sigom.domain.model.Event;
import me.sigom.domain.model.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;


@Service
public class EventStore implements EventStoreDB{

    private static final Logger logger = LoggerFactory.getLogger(EventStore.class);

    private final EventRepository eventRepository;

    private final SnapshotRepository snapshotRepository;
    private final EventBus eventBus;

    public EventStore(EventRepository eventRepository, SnapshotRepository snapshotRepository, EventBus eventBus) {
        this.eventRepository = eventRepository;
        this.snapshotRepository = snapshotRepository;
        this.eventBus = eventBus;
    }

    @Override
    public <T extends AggregateRoot> Mono<Void> save(T aggregate) {
           logger.info("Saving Event with aggregateId = " + aggregate.toString());
           Mono<Event> result = eventRepository.save(aggregate.getChanges());
           result.subscribe(eventBus::publish, Throwable::printStackTrace);
           return Mono.create(s -> {logger.info("event successfully persisted "); s.success();});
    }
    @Override
    public <T extends AggregateRoot> Mono<T> load(String aggregateId, Class<T> aggregateType) {
          logger.info("Find snapshot by aggregateId = " + aggregateId);
          Mono<T> snapShotMono = snapshotRepository.findByAggregateId(aggregateId)
                  .map(s -> getSnapshotFromClass(s, aggregateId, aggregateType));
          snapShotMono.subscribe(e -> logger.info("Loading snapshot"), Throwable::printStackTrace);
          return snapShotMono;
    }

    @Override
    public Mono<Boolean> exists(String aggregateId) {
        return null;
    }


    private Mono<Event> saveEvent(AggregateRoot aggregate) {
        Mono<Event> eventMono = eventRepository.save(aggregate.getChanges());
        eventMono.subscribe(e -> logger.info("Event persisted"), Throwable::printStackTrace);
        return eventMono;
    }

    private void saveSnapshot(AggregateRoot aggregate) {
        logger.info("saveSnapshot (SAVE SNAPSHOT) from aggregate >>>>>> " + aggregate.toString());
        aggregate.toSnapshot();
        Snapshot snapshot = EventSourcingUtils.snapshotFromAggregate(aggregate);
        logger.info("saveSnapshot (SAVE SNAPSHOT) to persist >>>>>> " + snapshot.toString());
        snapshotRepository.findByAggregateId(snapshot.getAggregateId())
                .doOnError(e -> snapshotRepository.save(snapshot))
                .map( c -> {
                              c.setData(aggregate.getChanges().getData());
                              return c;
                        })
                .flatMap(c -> snapshotRepository.save(snapshot))

                .subscribe(s -> logger.info("Snapshot persisted"), Throwable::printStackTrace);
    }
    private <T extends AggregateRoot> T getSnapshotFromClass(Snapshot snapshot, String aggregateId, Class<T> aggregateType) {
        if (snapshot == null) {
            final var defaultSnapshot = EventSourcingUtils.snapshotFromAggregate(getAggregate(aggregateId, aggregateType));
            return EventSourcingUtils.aggregateFromSnapshot(defaultSnapshot, aggregateType);
        }
        return EventSourcingUtils.aggregateFromSnapshot(snapshot, aggregateType);
    }
    private <T extends AggregateRoot> T getAggregate(final String aggregateId, final Class<T> aggregateType) {
        try {
            return aggregateType.getConstructor(String.class).newInstance(aggregateId);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
