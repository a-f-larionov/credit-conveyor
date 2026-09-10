package ru.creditbank.credit.operations.event;

import java.util.UUID;

public record CreditCreatedEvent(UUID creditId, UUID userId) {

}
