package dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionFactory {
    private static final Map<String, String> ENV_CACHE = loadEnvFile();

    private static final String HOST = requireEnv("DB_HOST");
    private static final String PORT = requireEnv("DB_PORT");
    private static final String DATABASE = requireEnv("DB_NAME");
    private static final String USER = requireEnv("DB_USER");
    private static final String PASSWORD = requireEnv("DB_PASSWORD");

    private static final String URL = String.format(
        "jdbc:postgresql://%s:%s/%s?sslmode=require",
        HOST,
        PORT,
        DATABASE
    );

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = ENV_CACHE.get(key);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(String.format(
                "Variavel de ambiente %s nao definida. Configure DB_HOST, DB_PORT, DB_NAME, DB_USER e DB_PASSWORD antes de iniciar o backend.",
                key
            ));
        }
        return value;
    }

    public static Map<String, String> loadEnvFile() {
        Map<String, String> values = new HashMap<>();
        Path envPath = Path.of(System.getProperty("user.dir"), ".env");
        if (!Files.exists(envPath)) {
            return values;
        }
        try {
            Files.lines(envPath)
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    int idx = line.indexOf('=');
                    if (idx > 0) {
                        String key = line.substring(0, idx).trim();
                        String val = line.substring(idx + 1).trim();
                        values.putIfAbsent(key, val);
                    }
                });
        } catch (IOException e) {
            System.err.println("[DB] Nao foi possivel ler o arquivo .env: " + e.getMessage());
        }
        return values;
    }
}

