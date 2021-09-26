package db.calc.portfolio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@linkplain FileBasedPortfolioProvider}.
 * It creates temporary file and reuses it for several tests.
 * The file is removed automatically.
 */
public class FileBasedPortfolioProviderTest {

    private static File tempFile;

    private PortfolioProvider provider;

    @BeforeClass
    public static void createTempFile() throws IOException {
        tempFile = File.createTempFile("temp_portfolio_", ".tmp");
        tempFile.deleteOnExit();
    }

    @Before
    public void setup() throws IOException {
        insertPositions(""); // clear portfolio file
        provider = new FileBasedPortfolioProvider(tempFile.getAbsolutePath());
    }

    @Test(expected = PortfolioProcessingException.class)
    public void testMissingFile() {
        new FileBasedPortfolioProvider("NON_EXISTING_FILE" + System.currentTimeMillis());
    }

    @Test
    public void testInfo() {
        String info = provider.getInfo();
        Assert.assertNotNull(info);
        Assert.assertFalse(info.isEmpty());
    }

    @Test
    public void testEmptyFile() {
        Set<PortfolioPosition> portfolio = provider.getPortfolio();
        Assert.assertNotNull(portfolio);
        Assert.assertTrue(portfolio.isEmpty());
    }

    @Test
    public void testAccumulation() throws IOException {
        Set<PortfolioPosition> expected = getPositions(pp("ABC", 3), pp("XYZ", 5));
        validate(expected, "ABC=1", "ABC=2", "XYZ=5");
    }

    @Test
    public void testParsing() throws IOException {
        Set<PortfolioPosition> expected = getPositions(pp("ABC", 3), pp("XYZ", 5));
        validate(expected, "ABC=1", "ABC=2", "ABC==5", "XYZ=5"); // "ABC==5" should be ignored
        validate(expected, "ABC=1", "ABC=2", "ABC=-5", "XYZ=5"); // "ABC=-5" should be ignored
        validate(expected, "ABC=1", "ABC=2", "ABC=one", "XYZ=5"); // "ABC=-5" should be ignored
        validate(expected, "ABC=1", "ABC=2", "ABC=5 ABC=10", "XYZ=5"); // "ABC=5 ABC=10" should be ignored
        validate(expected, "ABC=1", " ABC=2 ", "XYZ=5", "XYZ", "=", "1"); // "XYZ=5", "XYZ", "=", "1" should be ignored
        validate(expected, "ABC=1", " ABC=2 ", "XYZ=5");
        validate(expected, "ABC=1\n", " ABC=2 ", "XYZ=5");
    }

    /**
     * It populates file with given lines and validates if retrieved portfolio matches expected one. 
     */
    private void validate(Set<PortfolioPosition> expected, String... lines) throws IOException {
        insertPositions(lines);
        Set<PortfolioPosition> actual = provider.getPortfolio();
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }

    /**
     * Creates portfolio with given positions.
     */
    private Set<PortfolioPosition> getPositions(PortfolioPosition... pos) {
        return Arrays.stream(pos).collect(Collectors.toSet());
    }

    /**
     * Creates new position with given values.
     */
    private PortfolioPosition pp(String symbol, int quantity) {
        return new PortfolioPosition(symbol, quantity);
    }

    /**
     * Inserts entries/lines to portfolio file.
     */
    private void insertPositions(String... lines) throws IOException {
        Files.write(tempFile.toPath(), Arrays.asList(lines), StandardOpenOption.TRUNCATE_EXISTING);
    }
}
