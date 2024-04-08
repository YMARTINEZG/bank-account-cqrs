package me.sigom.adapter.out.persistence;

import me.sigom.domain.model.Event;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface EventRepository extends R2dbcRepository<Event, UUID> {
    Mono<Event> findByAggregateId(String aggregateId);
}
