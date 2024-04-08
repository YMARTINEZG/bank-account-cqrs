package me.sigom.adapter.out.persistence;

import me.sigom.domain.model.Snapshot;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SnapshotRepository extends R2dbcRepository<Snapshot, UUID> {
    Mono<Snapshot> findByAggregateId(String aggregateId);
}
