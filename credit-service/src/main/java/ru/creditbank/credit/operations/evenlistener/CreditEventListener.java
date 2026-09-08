package ru.creditbank.credit.operations.evenlistener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.creditbank.credit.operations.event.CreditCreatedEvent;
import ru.creditbank.credit.operations.service.DecisionService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreditEventListener {

    private final DecisionService decisionService;

    @Async
    @EventListener
    public void handleCreditCreated(CreditCreatedEvent event) {
        decisionService.decide((UUID) event.getSource());
    }
}