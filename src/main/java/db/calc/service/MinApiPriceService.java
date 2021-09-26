package db.calc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Implementation of {@linkplain PriceService} that uses <a href="https://min-api.cryptocompare.com/documentation"> min-api API.
 * To log request/response, set logging to Level.FINE.
 */
public class MinApiPriceService implements PriceService {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String MIN_API_URL = "https://min-api.cryptocompare.com/data/price";
    private static final String HTTP_GET_METHOD = "GET";
    private static final int CONNECTION_TIMEOUT_MS = 5000; // 5 seconds
    private static final int READ_TIMEOUT_MS = 5000; // 5 seconds
    private static final int HTTP_OK = 200;
    private static final Pattern RESPONSE_PATTERN = Pattern.compile("\\{\"(\\w+)\":(.+)\\}"); // e.g. {"EUR":10378.85}

    private final boolean withLogging;

    /**
     * Creates new instance.
     */
    public MinApiPriceService() {
        this.withLogging = false;
    }

    /**
     * Creates new instance with optional logging.
     */
    public MinApiPriceService(boolean withLogging) {
        this.withLogging = withLogging;
    }

    @Override
    public String getInfo() {
        return "Getting price information from " + MIN_API_URL + "\n";
    }

    @Override
    public BigDecimal getPrice(String symbol, String ccy) throws PriceServiceException, PriceNotFoundException {
        validate(symbol, ccy);
        URL minApi = buildUrl(symbol, ccy);
        out("Request:" + minApi.toString());
        try {
            String response = readResponse(sendRequest(minApi));
            out("Response:" + response);
            return extractPrice(ccy, response);
        } catch (IOException e) {
            // most likely service is unreachable
            throw new PriceServiceException("Cannot connect to " + MIN_API_URL, e);
        }
    }

    /**
     * Validates symbol and currency.
     */
    private void validate(String symbol, String ccy) {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("symbol must be provided");
        }
        if (ccy == null || ccy.isEmpty()) {
            throw new IllegalArgumentException("currency must be provided");
        }
    }

    /**
     * Sends request using given URL.
     */
    private HttpsURLConnection sendRequest(URL minApi) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) minApi.openConnection();
        connection.setRequestMethod(HTTP_GET_METHOD);
        connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestProperty("Accept", CONTENT_TYPE_JSON);
        connection.connect();
        // only 200 expected
        if (connection.getResponseCode() != HTTP_OK) {
            throw new PriceServiceException(
                    "Unexpected HTTP Code: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
        }
        return connection;
    }

    /**
     * Reads response from given connection.
     */
    private String readResponse(HttpsURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        return reader.lines().collect(Collectors.joining());
    }

    /**
     * Extracts the price for given currency from response string.
     */
    private BigDecimal extractPrice(String ccy, String response) throws PriceNotFoundException {
        // use simple pattern to check the response and extract price
        Matcher matcher = RESPONSE_PATTERN.matcher(response);
        if (matcher.matches() && matcher.group(1).equals(ccy)) {
            try {
                return new BigDecimal(matcher.group(2));
            } catch (NumberFormatException e) {
                System.err.printf("Cannot extract price from [%s]. %s", response, e.getMessage());
            }
        }
        throw new PriceNotFoundException("Cannot extract price from " + response);
    }

    /**
     * Builds request URL for given symbol and currency.
     */
    private URL buildUrl(String symbol, String ccy) {
        StringBuilder sb = new StringBuilder(MIN_API_URL);
        sb.append("?fsym=").append(symbol).append("&tsyms=").append(ccy);
        try {
            return new URL(sb.toString());
        } catch (MalformedURLException e) {
            throw new PriceServiceException("Invalid URL " + sb.toString(), e);
        }
    }

    /**
     * Logs message to standard output if logging is enabled.
     */
    private void out(String format, Object... args) {
        if (withLogging) {
            System.out.println(String.format(format, args));
        }
    }
}
