package db.calc.portfolio;

import java.util.Set;

/**
 * Provides positions of the portfolio.
 */
public interface PortfolioProvider {

    /**
     * Returns portfolio as collection of accumulated positions.
     * Positions with the same symbol are merged and their quantities are added.
     *
     * @return positions
     */
    Set<PortfolioPosition> getPortfolio();

    /**
     * Gets information about implementation. 
     * @return
     */
    String getInfo();
}
