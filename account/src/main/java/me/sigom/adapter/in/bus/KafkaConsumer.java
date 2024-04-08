package me.sigom.adapter.in.bus;


import me.sigom.adapter.out.persistence.BankAccountDocumentRepository;
import me.sigom.application.events.AddressUpdatedEvent;
import me.sigom.application.events.BalanceDepositedEvent;
import me.sigom.application.events.BankAccountCreatedEvent;
import me.sigom.common.utils.SerializerUtils;
import me.sigom.domain.model.BankAccountDocument;
import me.sigom.domain.model.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class KafkaConsumer implements Projection{

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private final BankAccountDocumentRepository bankRepository;

    public KafkaConsumer(BankAccountDocumentRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @KafkaListener(topics = "${application.configs.topic.name}", groupId="${application.configs.group.name}")
    public void process(Message<byte[]> message) {
        logger.info("kafka  (CONSUME) EVENT >>>>>> ");
        final Event event = SerializerUtils.deserializeEventsFromJsonBytes(message.getPayload());
        this.when(event);
    }


    public void when(Event event) {
        final var aggregateId = event.getAggregateId();
        logger.info("(when) >>>>> aggregateId: " + aggregateId);

        switch (event.getEventType()) {
            case BankAccountCreatedEvent.BANK_ACCOUNT_CREATED -> {
                handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BankAccountCreatedEvent.class));
            }

            case AddressUpdatedEvent.ADDRESS_UPDATED -> {
                handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), AddressUpdatedEvent.class));
            }
            case BalanceDepositedEvent.BALANCE_DEPOSITED -> {
                handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BalanceDepositedEvent.class));
            }
            default -> {
                return;
            }
        }
    }

    private void handle(BankAccountCreatedEvent event) {
        logger.info("(when) BankAccountCreatedEvent: " + event  + " aggregateID: " + event.getAggregateId());

        final var document = BankAccountDocument.builder()
                .aggregateId(event.getAggregateId())
                .email(event.getEmail())
                .address(event.getAddress())
                .userName(event.getUserName())
                .balance(BigDecimal.valueOf(0))
                .build();
        logger.info("document to persist: " + document.toString() );
        bankRepository.save(document).log().subscribe();
    }
    private void handle(AddressUpdatedEvent event) {
        logger.info("(when) AddressUpdatedEvent: " + event  + " aggregateID: " + event.getAggregateId());
    }

    private void handle(BalanceDepositedEvent event) {
        logger.info("(when) BalanceDepositedEvent: " + event + " aggregateID: " + event.getAggregateId());
    }
}
