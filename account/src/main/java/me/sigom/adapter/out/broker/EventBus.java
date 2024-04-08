package me.sigom.adapter.out.broker;

import io.smallrye.mutiny.Uni;
import me.sigom.domain.model.Event;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventBus {
    Uni<Void> publish(Event event);
}
