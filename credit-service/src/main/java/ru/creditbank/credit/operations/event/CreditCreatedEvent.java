package ru.creditbank.credit.operations.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class CreditCreatedEvent extends ApplicationEvent {
    public CreditCreatedEvent(UUID creditId) {
        super(creditId);
    }
}
