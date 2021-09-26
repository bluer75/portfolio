package db.calc.service;

/**
 * Exception indicating problem with {@link PriceService}.
 */
@SuppressWarnings("serial")
public class PriceServiceException extends RuntimeException {

    /**
     * Constructs new exception with given message.
     *
     * @param message
     */
    public PriceServiceException(String message) {
        super(message);
    }

    /**
     * Constructs new exception with given message and cause.
     *
     * @param message
     * @param cause
     */
    public PriceServiceException(String message, Exception cause) {
        super(message, cause);
    }
}
