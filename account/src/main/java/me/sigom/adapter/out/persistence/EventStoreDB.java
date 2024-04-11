package me.sigom.adapter.out.persistence;

import io.smallrye.mutiny.Uni;
import me.sigom.domain.model.AggregateRoot;
import me.sigom.domain.model.Event;
import reactor.core.publisher.Mono;


public interface EventStoreDB {
    <T extends AggregateRoot> Mono<String> save(final T aggregate);
    <T extends AggregateRoot> Mono<T> load(final String aggregateId, final Class<T> aggregateType);
    Mono<Boolean> exists(final String aggregateId);

}
