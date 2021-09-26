package db.calc.portfolio;

/**
 * Exception indicating problem when processing portfolio.
 */
@SuppressWarnings("serial")
public class PortfolioProcessingException extends RuntimeException {

    /**
     * Constructs new exception with given message.
     *
     * @param message
     */
    public PortfolioProcessingException(String message) {
        super(message);
    }

    /**
     * Constructs new exception with given message and cause.
     *
     * @param message
     * @param cause
     */
    public PortfolioProcessingException(String message, Exception cause) {
        super(message, cause);
    }
}
