package ru.creditbank.credit.operations.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ScoreCreditUpdatedEvent {
    private final UUID creditId;

    public ScoreCreditUpdatedEvent(UUID creditId) {
        this.creditId = creditId;
    }
}
