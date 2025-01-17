package util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public class SimpleLogger {
    private static SimpleLogger instance;
    private final Map<Class<?>, Logger> loggers = new HashMap<>();

    public static SimpleLogger getInstance() {
        if (instance == null) {
            instance = new SimpleLogger();
        }
        return instance;
    }

    public Logger getLogger(Class<?> clazz) {
        // Reutilizar logger si ya existe
        Logger logger = loggers.get(clazz);
        if (logger == null) {
            logger = Logger.getLogger(clazz.getSimpleName());

            // Eliminar handlers anteriores si los hay
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            // Eliminar handlers del logger raíz
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // Configurar el nuevo handler para la consola
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(getFormatter());

            // Añadir el handler y deshabilitar los handlers de los padres
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);

            // Guardar el logger en el mapa para su reutilización
            loggers.put(clazz, logger);
        }

        return logger;
    }

    private Formatter getFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String level = record.getLevel().toString();
                String name = record.getLoggerName();
                String message = record.getMessage();
                if (record.getParameters() != null) {
                    message = MessageFormat.format(message, record.getParameters());
                }

                String color = getColorForLevel(level);

                return String.format("%s[%s] [%s] [%s] - %s%s\n", color, timestamp, level, name, message, "\u001B[0m");
            }

            // Returns a color depending of the severity.
            private String getColorForLevel(String level) {
                return switch (level) {
                    case "SEVERE" -> "\u001B[31m"; // Red
                    case "WARNING" -> "\u001B[33m"; // Yellow
                    case "INFO" -> "\u001B[32m"; // Green
                    case "FINE", "FINER", "FINEST" -> "\u001B[34m"; // Blue
                    default -> "\u001B[37m"; // Default -> white
                };
            }
        };
    }
}
