package db.calc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;

import db.calc.portfolio.FileBasedPortfolioProvider;
import db.calc.portfolio.PortfolioPosition;
import db.calc.portfolio.PortfolioProvider;
import db.calc.service.MinApiPriceService;
import db.calc.service.PriceNotFoundException;
import db.calc.service.PriceService;

/**
 * Simple calculator evaluating the value of given portfolio.
 * It processes positions from given file and uses price service to evaluate the value of that position.
 * <p>Default portfolio currency is EUR and portfolio file name is bobs_crypto.txt.
 * These values can be overridden with -c CURRENCY and -f FILE_NAME parameters.
 * <p>Price is retrieved from external service <a href="https://min-api.cryptocompare.com/documentation"> min-api.cryptocompare.com</a>.
 * <p>If proxy is required to connect to Internet, this can be specified with following properties:
 * -Dhttps.proxyHost=PROXY_HOST
 * -Dhttps.proxyPort=PROXY_PORT
 * <p><b>Please note that performance optimisations and thread safety aspects were not evaluated with this implementation.
 */
public class PortfolioCalculator {

    private static final String DEFAULT_CCY = "EUR";
    private static final String DEFAULT_FILE_NAME = "bobs_crypto.txt";
    private static final String CCY_OPTION = "-c";
    private static final String FILE_NAME_OPTION = "-f";
    private static final String VERBOSE_OPTION = "-v";
    private final PriceService priceService;
    private final PortfolioProvider portfolioProvider;

    /**
     * Creates instance of {@linkplain PortfolioCalculator} with given {@linkplain PriceService} and {@linkplain PortfolioProvider}.
     *
     * @param priceService
     * @param portfolioProvider
     */
    public PortfolioCalculator(PriceService priceService, PortfolioProvider portfolioProvider) {
        this.priceService = priceService;
        this.portfolioProvider = portfolioProvider;
        info(priceService.getInfo());
        info(portfolioProvider.getInfo());
    }

    /**
     * Calculates the total value of the portfolio in given currency.
     *
     * @param ccy currency
     * @return value of the portfolio
     */
    public BigDecimal calculate(String ccy) {
        // get price for each symbol and calculate total value
        BigDecimal total = portfolioProvider.getPortfolio().stream() //
                .map(pos -> eval(pos, ccy)) //
                .reduce(BigDecimal.ZERO, BigDecimal::add); //
        info("Total value: %s %s\n", total.toPlainString(), ccy);
        return total;
    }

    /**
     * Evaluates value of the position in given currency.
     */
    private BigDecimal eval(PortfolioPosition pos, String ccy) {
        try {
            info("Evaluating %s -> ", pos.toString());
            BigDecimal price = priceService.getPrice(pos.getSymbol(), ccy);
            info("Price: %s %s", price.toPlainString(), ccy);
            BigDecimal value = price.multiply(new BigDecimal(pos.getQuantity()));
            info(", Value %s %s \n", value.toPlainString(), ccy);
            return value;
        } catch (PriceNotFoundException e) {
            // this may indicate invalid symbol - ignore this position
            info("Skipping, price not available [%s]\n", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * @see PortfolioCalculator
     */
    public static void main(String... args) {
        String ccy = getOptionValue(args, CCY_OPTION, DEFAULT_CCY);
        String fileName = getOptionValue(args, FILE_NAME_OPTION, DEFAULT_FILE_NAME);
        boolean withLogging = hasOption(args, VERBOSE_OPTION);
        try {
            PortfolioCalculator calculator = new PortfolioCalculator(new MinApiPriceService(withLogging),
                    new FileBasedPortfolioProvider(fileName));
            calculator.calculate(ccy);
        } catch (Exception e) {
            e.printStackTrace();
            info("Please check README.md");
        }
    }

    /**
     * Gets given option from command line parameters or returns its default value.
     */
    private static String getOptionValue(String[] args, String option, String defaultValue) {
        Arrays.stream(args).iterator();
        for (int i = 0; i < args.length; i++) {
            if (option.equals(args[i]) && i < args.length - 1) {
                return args[i + 1];
            }
        }
        return defaultValue;
    }

    /**
     * Checks if given option is provided in command line parameters.
     */
    private static boolean hasOption(String[] args, String option) {
        return Arrays.stream(args).anyMatch(arg -> Objects.equals(arg, option));
    }

    /**
     * Logs message to standard output.
     */
    private static void info(String format, Object... args) {
        System.out.printf(format, args);
    }
}
