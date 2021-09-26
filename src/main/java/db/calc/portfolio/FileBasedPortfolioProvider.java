package db.calc.portfolio;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of  {@linkplain PortfolioProvider} that reads positions from external file.
 */
public class FileBasedPortfolioProvider implements PortfolioProvider {

    // path to the file with positions
    private final Path path;

    /**
     * Creates new instance for given file.
     *
     * @param fileName name of the file with absolute path
     */
    public FileBasedPortfolioProvider(String fileName) {
        path = Paths.get(fileName);
        if (!Files.exists(path)) {
            throw new PortfolioProcessingException("Cannot read portfolio file " + path.toAbsolutePath());
        }
    }

    @Override
    public Set<PortfolioPosition> getPortfolio() {
        try {
            // collect all valid positions and calculate total quantity for each symbol
            Map<String, Integer> portfolio = Files.lines(path) //
                    .map(FileBasedPortfolioProvider::getPositionOrEmptyValue) //
                    .filter(Optional::isPresent) //
                    .map(Optional::get) //
                    .collect( //
                            groupingBy(PortfolioPosition::getSymbol, summingInt(PortfolioPosition::getQuantity)));
            // convert accumulated portfolio to list
            return portfolio.entrySet().stream().map(e -> new PortfolioPosition(e.getKey(), e.getValue()))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new PortfolioProcessingException("Cannot read portfolio file " + path.toAbsolutePath(), e);
        }
    }

    @Override
    public String getInfo() {
        return "Reading portfolio from " + path.toAbsolutePath() + "\n";
    }

    /**
     * If provided string contains symbol and quantity and can be parsed to valid {@linkplain PortfolioPosition},
     * then new instance is created otherwise empty value is returned.
     *
     * @param source
     * @return PortfolioPosition or empty value
     */
    private static Optional<PortfolioPosition> getPositionOrEmptyValue(String source) {
        if (source != null && !source.trim().isEmpty()) {
            // simply split input string around "=" and extract symbol and quantity
            String[] values = source.split("=");
            if (values != null && values.length == 2) {
                try {
                    return Optional.of(new PortfolioPosition(values[0].trim(), Integer.valueOf(values[1].trim())));
                } catch (IllegalArgumentException e) {
                    err("Cannot create position from %s", Arrays.toString(values));
                    return Optional.empty();
                }
            }
            err("Cannot create position from [%s]", source);
        }
        return Optional.empty();
    }

    /**
     * Logs message to standard error output.
     */
    private static void err(String format, Object... args) {
        System.err.println(String.format(format, args));
    }
}
