package db.calc.portfolio;

/**
 * Represents position with symbol and quantity.
 */
public class PortfolioPosition {

    private final String symbol;
    private final int quantity;

    /**
     * Creates new PortfolioPosition with given symbol and quantity.
     *
     * @param symbol
     * @param quantity
     * @throws IllegalArgumentException if symbol is empty or quantity is negative
     */
    public PortfolioPosition(String symbol, int quantity) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Invalid symbol [" + symbol + "]");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Invalid quantity [" + quantity + "]");
        }
        this.symbol = symbol;
        this.quantity = quantity;
    }

    /**
     * @return the symbol
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "PortfolioPosition[" + symbol + "|" + quantity + "]";
    }

    // generated methods

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + quantity;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PortfolioPosition other = (PortfolioPosition) obj;
        if (quantity != other.quantity)
            return false;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        return true;
    }
}
