package db.calc.portfolio;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@linkplain PortfolioPosition}.
 */
public class PortfolioPositionTest {

    @Test
    public void testValues() {
        String symbol = "ABC";
        int quantity = 175;
        PortfolioPosition actual = new PortfolioPosition(symbol, quantity);
        Assert.assertEquals(symbol, actual.getSymbol());
        Assert.assertEquals(quantity, actual.getQuantity());
    }

    @Test
    public void testValidation() {
        Object[][] values = { { null, 0 }, { "", 1 }, { "ABC", -1 } };
        for (Object[] value : values) {
            try {
                new PortfolioPosition((String) value[0], (Integer) value[1]);
            } catch (IllegalArgumentException e) {
                // expected
                continue;
            }
            Assert.fail("Expected validation error for " + Arrays.toString(value));
        }
    }
}
