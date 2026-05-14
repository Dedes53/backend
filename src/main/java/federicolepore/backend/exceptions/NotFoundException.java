package federicolepore.backend.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entityType) {
        super("Non è stato possibile trovare l'entità di tipo " + entityType + " richiesta");
    }
}
