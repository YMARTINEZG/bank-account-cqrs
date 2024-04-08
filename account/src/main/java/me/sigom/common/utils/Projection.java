package me.sigom.common.utils;

import me.sigom.domain.model.Event;
import reactor.core.publisher.Mono;

public interface Projection {
    Mono<Void> when(Event event);
}
