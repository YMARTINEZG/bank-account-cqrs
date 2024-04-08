package me.sigom.adapter.out.broker;

import io.smallrye.mutiny.Uni;
import me.sigom.common.utils.SerializerUtils;
import me.sigom.domain.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaEventBus implements EventBus{
    private static final int PUBLISH_TIMEOUT = 1000;
    private static final int BACKOFF_TIMEOUT = 300;
    private static final int RETRY_COUNT = 3;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventBus.class);

    public KafkaEventBus(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${application.configs.topic.name}")
    private String TOPIC_NAME;
    @Override
    public Uni<Void> publish(Event event) {
        final byte[] eventBytes = SerializerUtils.serializeToJsonBytes(event);
        CompletableFuture<SendResult<String, byte[]>> future = kafkaTemplate.send(TOPIC_NAME, event.getAggregateId(), eventBytes);
        return Uni.createFrom().completionStage(future)
                .ifNoItem().after(Duration.ofMillis(PUBLISH_TIMEOUT)).fail()
                .onFailure().invoke(Throwable::printStackTrace)
                .onFailure().retry().withBackOff(Duration.of(BACKOFF_TIMEOUT, ChronoUnit.MILLIS)).atMost(RETRY_COUNT)
                .onItem().invoke(msg -> logger.info("publish key: " + event.getAggregateId()))
                .replaceWithVoid();
    }
}
