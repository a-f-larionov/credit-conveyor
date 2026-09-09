package ru.creditbank.credit.operations.decision.rule;

import ru.creditbank.credit.operations.dto.ScoringInputDto;

public interface CreditScoreRule {

    Long evaluate(ScoringInputDto scoringInputDto);

    String getDescription();
}