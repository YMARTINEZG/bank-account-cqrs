package me.sigom.adapter.out.persistence;



import me.sigom.domain.model.BankAccountDocument;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface BankAccountDocumentRepository extends R2dbcRepository<BankAccountDocument, String> {
    Mono<Void> deleteByAggregateId(String aggregateId);
}
