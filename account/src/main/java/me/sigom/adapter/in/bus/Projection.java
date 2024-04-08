package me.sigom.adapter.in.bus;

import io.smallrye.mutiny.Uni;
import me.sigom.domain.model.BankAccountDocument;
import me.sigom.domain.model.Event;
import reactor.core.publisher.Mono;

public interface Projection {
    void when(Event event);
}
