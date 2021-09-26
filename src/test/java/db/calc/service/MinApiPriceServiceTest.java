package db.calc.service;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@linkplain MinApiPriceService}.
 */
public class MinApiPriceServiceTest {

    private PriceService service;

    @Before
    public void setup() {
        service = new MinApiPriceService();
    }

    @Test
    public void testInfo() {
        String info = service.getInfo();
        Assert.assertNotNull(info);
        Assert.assertFalse(info.isEmpty());
    }

    @Test
    public void testValidation() throws PriceServiceException, PriceNotFoundException {
        String[][] values = { { null, null }, { null, "" }, { "", "" }, { "", null }, { "ABC", null },
                { null, "ABC" } };
        for (String[] value : values) {
            try {
                service.getPrice(value[0], value[1]);
            } catch (IllegalArgumentException e) {
                // expected
                continue;
            }
            Assert.fail("Expected validation error for " + Arrays.toString(value));
        }
    }
}
