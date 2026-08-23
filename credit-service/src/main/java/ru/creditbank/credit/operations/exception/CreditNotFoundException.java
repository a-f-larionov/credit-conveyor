package ru.creditbank.credit.operations.exception;

public class CreditNotFoundException extends RuntimeException{

    public CreditNotFoundException(String msg) {
        super(msg);
    }
}
