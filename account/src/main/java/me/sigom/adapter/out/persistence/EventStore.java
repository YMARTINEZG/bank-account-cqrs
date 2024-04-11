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
    public <T extends AggregateRoot> Mono<String> save(T aggregate) {
        return this.saveEvent(aggregate.getChanges())
                .flatMap(this::saveSnapshot)
                .doOnSuccess(s -> eventBus.publish(aggregate.getChanges()))
                .flatMap(s -> Mono.just(aggregate.getId()));
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
        return snapshotRepository.findByAggregateId(aggregateId).hasElement();
    }

    private Mono<Event> saveEvent(Event event) {
        return eventRepository.save(event);
    }

    private Mono<Snapshot> saveSnapshot(Event event) {
        Snapshot snapshot = EventSourcingUtils.snapshotFromEvent(event);
        logger.info("saveSnapshot (SAVE SNAPSHOT) to persist >>>>>> " + snapshot.toString());
        return this.exists(snapshot.getAggregateId()).flatMap(exist -> exist ? Mono.error(new Exception("Snapshot already in use"))
                : snapshotRepository.save(snapshot)
        );
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
