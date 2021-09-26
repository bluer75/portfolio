package db.calc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import db.calc.portfolio.FileBasedPortfolioProvider;
import db.calc.service.PriceNotFoundException;
import db.calc.service.PriceService;
import db.calc.service.PriceServiceException;

/**
 * Tests for {@linkplain PortfolioCalculator}.
 * It creates temporary file that gets removed automatically.
 */
public class PortfolioCalculatorTest {

    // prices symbol -> currency -> price
    private static Map<String, Map<String, BigDecimal>> prices;
    private static File tempFile;

    @BeforeClass
    public static void setup() throws IOException {
        tempFile = File.createTempFile("temp_portfolio_", ".tmp");
        tempFile.deleteOnExit();
        prices = new HashMap<>();
        prices.put("ABC", new HashMap<>());
        prices.get("ABC").put("EUR", new BigDecimal(10));
        prices.get("ABC").put("USD", new BigDecimal(15));
        prices.put("XYZ", new HashMap<>());
        prices.get("XYZ").put("EUR", new BigDecimal(20));
        prices.get("XYZ").put("USD", new BigDecimal(25));
    }

    // test price service
    private PriceService priceService = new PriceService() {

        @Override
        public BigDecimal getPrice(String symbol, String ccy) throws PriceServiceException, PriceNotFoundException {
            try {
                return prices.get(symbol).get(ccy);
            } catch (Exception e) {
                Assert.fail("Price not found");
            }
            return BigDecimal.ZERO;
        }

        @Override
        public String getInfo() {
            return "TestPriceService";
        }
    };

    @Test
    public void testCalculate() throws IOException {
        insertPositions(tempFile, "ABC=5", "XYZ=10");
        PortfolioCalculator calc = new PortfolioCalculator(priceService,
                new FileBasedPortfolioProvider(tempFile.getAbsolutePath()));
        Assert.assertEquals(new BigDecimal((5 * 10) + (10 * 20)), calc.calculate("EUR"));
        Assert.assertEquals(new BigDecimal((5 * 15) + (10 * 25)), calc.calculate("USD"));
    }

    /**
     * Inserts entries/lines to portfolio file.
     */
    private void insertPositions(File tempFile, String... lines) throws IOException {
        Files.write(tempFile.toPath(), Arrays.asList(lines), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
