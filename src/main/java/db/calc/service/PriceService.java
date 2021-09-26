package db.calc.service;

import java.math.BigDecimal;

/**
 * Service that provides prices for cryptocurrencies.
 */
public interface PriceService {

    /**
     * Gets the current price of given cryptocurrency in other currency.
     * It throws {@link PriceServiceException} if price cannot be retrieved.
     *
     * @param symbol cryptocurrency
     * @param ccy currency
     * @return current price
     * @throws PriceServiceException if service is not reachable
     * @throws PriceNotFoundException if symbol or currency is not valid
     */
    BigDecimal getPrice(String symbol, String ccy) throws PriceServiceException, PriceNotFoundException;

    /**
     * Gets information about implementation. 
     * @return
     */
    String getInfo();
}
