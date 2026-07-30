import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


// ============================================================================
// 1. LOG LEVEL
// ============================================================================

enum LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
}


// ============================================================================
// 2. LOG MESSAGE
// ============================================================================

class LogMessage {

    private final LogLevel level;
    private final String message;
    private final LocalDateTime timestamp;

    public LogMessage(LogLevel level, String message) {
        this.level = level;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}


// ============================================================================
// 3. FORMATTER - STRATEGY PATTERN
// ============================================================================

interface LogFormatter {

    String format(LogMessage logMessage);
}


// ----------------------------------------------------------------------------
// Text Formatter
// ----------------------------------------------------------------------------

class TextLogFormatter implements LogFormatter {

    @Override
    public String format(LogMessage logMessage) {

        return "[" + logMessage.getTimestamp() + "] "
                + "[" + logMessage.getLevel() + "] "
                + logMessage.getMessage();
    }
}


// ----------------------------------------------------------------------------
// JSON Formatter
// ----------------------------------------------------------------------------

class JsonLogFormatter implements LogFormatter {

    @Override
    public String format(LogMessage logMessage) {

        return "{"
                + "\"timestamp\":\"" + logMessage.getTimestamp() + "\","
                + "\"level\":\"" + logMessage.getLevel() + "\","
                + "\"message\":\"" + logMessage.getMessage() + "\""
                + "}";
    }
}


// ============================================================================
// 4. APPENDER - OBSERVER
// ============================================================================

interface LogAppender {

    void append(LogMessage logMessage);
}


// ----------------------------------------------------------------------------
// Console Appender
// ----------------------------------------------------------------------------

class ConsoleAppender implements LogAppender {

    private final LogFormatter formatter;

    public ConsoleAppender(LogFormatter formatter) {
        this.formatter = formatter;
    }

    /*
     * Multiple threads may try to write to the same console.
     *
     * synchronized guarantees that only one thread at a time
     * performs this write operation.
     */
    @Override
    public synchronized(LogMessage logMessage) {

        String formattedLog =
                formatter.format(logMessage);

        System.out.println(formattedLog);
    }
}


// ----------------------------------------------------------------------------
// File Appender
// ----------------------------------------------------------------------------

class FileAppender implements LogAppender {

    private final LogFormatter formatter;

    public FileAppender(LogFormatter formatter) {
        this.formatter = formatter;
    }

    /*
     * Multiple threads may try to write to the same file.
     *
     * synchronized guarantees that only one thread at a time
     * performs this write operation.
     */
    @Override
    public synchronized void append(LogMessage logMessage) {

        String formattedLog =
                formatter.format(logMessage);

        /*
         * In a real implementation:
         *
         * fileWriter.write(formattedLog);
         * fileWriter.newLine();
         * fileWriter.flush();
         *
         * For demonstration:
         */

        System.out.println(
                "[FILE] " + formattedLog
        );
    }
}


// ============================================================================
// 5. LOG HANDLER - CHAIN OF RESPONSIBILITY + SUBJECT
// ============================================================================

abstract class LogHandler {

    protected LogHandler nextHandler;

    /*
     * OBSERVER PATTERN
     *
     * Handler = Subject
     * Appenders = Observers
     *
     * CopyOnWriteArrayList makes iteration safe even if another
     * thread adds/removes an appender at runtime.
     */
    protected final List<LogAppender> appenders =
            new CopyOnWriteArrayList<>();


    // ------------------------------------------------------------------------
    // Chain configuration
    // ------------------------------------------------------------------------

    public void setNextHandler(LogHandler nextHandler) {
        this.nextHandler = nextHandler;
    }


    // ------------------------------------------------------------------------
    // Observer registration
    // ------------------------------------------------------------------------

    public void addAppender(LogAppender appender) {
        appenders.add(appender);
    }


    public void removeAppender(LogAppender appender) {
        appenders.remove(appender);
    }


    // ------------------------------------------------------------------------
    // Main Chain of Responsibility operation
    // ------------------------------------------------------------------------

    public void handle(LogMessage logMessage) {

        if (canHandle(logMessage.getLevel())) {

            notifyAppenders(logMessage);

            return;
        }

        if (nextHandler != null) {
            nextHandler.handle(logMessage);
        }
    }


    // ------------------------------------------------------------------------
    // Notify all observers
    // ------------------------------------------------------------------------

    protected void notifyAppenders(LogMessage logMessage) {

        for (LogAppender appender : appenders) {

            appender.append(logMessage);
        }
    }


    protected abstract boolean canHandle(LogLevel level);
}


// ============================================================================
// 6. CONCRETE LOG HANDLERS
// ============================================================================

class DebugLogHandler extends LogHandler {

    @Override
    protected boolean canHandle(LogLevel level) {

        return level == LogLevel.DEBUG;
    }
}


class InfoLogHandler extends LogHandler {

    @Override
    protected boolean canHandle(LogLevel level) {

        return level == LogLevel.INFO;
    }
}


class WarnLogHandler extends LogHandler {

    @Override
    protected boolean canHandle(LogLevel level) {

        return level == LogLevel.WARN;
    }
}


class ErrorLogHandler extends LogHandler {

    @Override
    protected boolean canHandle(LogLevel level) {

        return level == LogLevel.ERROR;
    }
}


class FatalLogHandler extends LogHandler {

    @Override
    protected boolean canHandle(LogLevel level) {

        return level == LogLevel.FATAL;
    }
}


// ============================================================================
// 7. LOGGER CONFIGURATION
// ============================================================================

class LoggerConfiguration {

    public LogHandler buildHandlerChain() {

        // --------------------------------------------------------------------
        // Formatters
        // --------------------------------------------------------------------

        LogFormatter textFormatter =
                new TextLogFormatter();

        LogFormatter jsonFormatter =
                new JsonLogFormatter();


        // --------------------------------------------------------------------
        // Appenders
        // --------------------------------------------------------------------

        LogAppender consoleAppender =
                new ConsoleAppender(textFormatter);

        LogAppender fileAppender =
                new FileAppender(jsonFormatter);


        // --------------------------------------------------------------------
        // Handlers
        // --------------------------------------------------------------------

        LogHandler debugHandler =
                new DebugLogHandler();

        LogHandler infoHandler =
                new InfoLogHandler();

        LogHandler warnHandler =
                new WarnLogHandler();

        LogHandler errorHandler =
                new ErrorLogHandler();

        LogHandler fatalHandler =
                new FatalLogHandler();


        // --------------------------------------------------------------------
        // Build Chain of Responsibility
        // --------------------------------------------------------------------

        debugHandler.setNextHandler(infoHandler);

        infoHandler.setNextHandler(warnHandler);

        warnHandler.setNextHandler(errorHandler);

        errorHandler.setNextHandler(fatalHandler);


        // --------------------------------------------------------------------
        // Register Appenders / Observers
        // --------------------------------------------------------------------

        /*
         * DEBUG
         *      ↓
         * Console
         */
        debugHandler.addAppender(
                consoleAppender
        );


        /*
         * INFO
         *      ↓
         * Console
         */
        infoHandler.addAppender(
                consoleAppender
        );


        /*
         * WARN
         *      ↓
         * Console
         */
        warnHandler.addAppender(
                consoleAppender
        );


        /*
         * ERROR
         *      ↓
         * Console
         * +
         * File
         */
        errorHandler.addAppender(
                consoleAppender
        );

        errorHandler.addAppender(
                fileAppender
        );


        /*
         * FATAL
         *      ↓
         * Console
         * +
         * File
         */
        fatalHandler.addAppender(
                consoleAppender
        );

        fatalHandler.addAppender(
                fileAppender
        );


        return debugHandler;
    }
}


// ============================================================================
// 8. LOGGER - SINGLETON
// ============================================================================

class Logger {

    private static volatile Logger instance;

    private final LogHandler firstHandler;


    // ------------------------------------------------------------------------
    // Private constructor
    // ------------------------------------------------------------------------

    private Logger() {

        LoggerConfiguration configuration =
                new LoggerConfiguration();

        this.firstHandler =
                configuration.buildHandlerChain();
    }


    // ------------------------------------------------------------------------
    // Thread-safe Singleton
    // ------------------------------------------------------------------------

    public static Logger getInstance() {

        if (instance == null) {

            synchronized (Logger.class) {

                if (instance == null) {

                    instance =
                            new Logger();
                }
            }
        }

        return instance;
    }


    // ------------------------------------------------------------------------
    // Generic Log Method
    // ------------------------------------------------------------------------

    public void log(
            LogLevel level,
            String message) {

        LogMessage logMessage =
                new LogMessage(
                        level,
                        message
                );

        firstHandler.handle(
                logMessage
        );
    }


    // ------------------------------------------------------------------------
    // Convenience APIs
    // ------------------------------------------------------------------------

    public void debug(String message) {

        log(
                LogLevel.DEBUG,
                message
        );
    }


    public void info(String message) {

        log(
                LogLevel.INFO,
                message
        );
    }


    public void warn(String message) {

        log(
                LogLevel.WARN,
                message
        );
    }


    public void error(String message) {

        log(
                LogLevel.ERROR,
                message
        );
    }


    public void fatal(String message) {

        log(
                LogLevel.FATAL,
                message
        );
    }
}


// ============================================================================
// 9. DRIVER
// ============================================================================

public class Main {

    public static void main(String[] args) {

        Logger logger =
                Logger.getInstance();


        // --------------------------------------------------------------------
        // DEBUG
        // Console only
        // --------------------------------------------------------------------

        logger.debug(
                "Starting payment processing"
        );


        // --------------------------------------------------------------------
        // INFO
        // Console only
        // --------------------------------------------------------------------

        logger.info(
                "Payment request received"
        );


        // --------------------------------------------------------------------
        // WARN
        // Console only
        // --------------------------------------------------------------------

        logger.warn(
                "Payment gateway response is slow"
        );


        // --------------------------------------------------------------------
        // ERROR
        // Console + File
        // --------------------------------------------------------------------

        logger.error(
                "Payment failed"
        );


        // --------------------------------------------------------------------
        // FATAL
        // Console + File
        // --------------------------------------------------------------------

        logger.fatal(
                "Payment service unavailable"
        );
    }
}
