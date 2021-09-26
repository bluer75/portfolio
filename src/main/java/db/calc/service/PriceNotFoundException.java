package db.calc.service;

/**
 * Exception indicating problem with symbol or currency.
 */
@SuppressWarnings("serial")
public class PriceNotFoundException extends Exception {

    /**
     * Constructs new exception with given message.
     *
     * @param message
     */
    public PriceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs new exception with given message and cause.
     *
     * @param message
     * @param cause
     */
    public PriceNotFoundException(String message, Exception cause) {
        super(message, cause);
    }
}
