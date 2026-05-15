package federicolepore.backend.exceptions;

import java.util.List;


public class PayloadValidationException extends RuntimeException {
    private List<String> errors;

    public PayloadValidationException(List<String> errors) {
        super("Alcuni errori sono avvenuti nel processo di validazione");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}

