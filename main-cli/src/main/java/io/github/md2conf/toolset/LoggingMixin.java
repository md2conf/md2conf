package io.github.md2conf.toolset;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import static picocli.CommandLine.Spec.Target.MIXEE;

public class LoggingMixin {
    /**
     * This mixin is able to climb the command hierarchy because the
     * {@code @Spec(Target.MIXEE)}-annotated field gets a reference to the command where it is used.
     */
    private @CommandLine.Spec(MIXEE)
    CommandLine.Model.CommandSpec mixee; // spec of the command where the @Mixin is used

    private boolean[] verbosity = new boolean[0];

    // Each subcommand that mixes in the LoggingMixin has its own instance of this class,
    // so there may be many LoggingMixin instances.
    // We want to store the verbosity value in a single, central place, so
    // we find the top-level command,
    // and store the verbosity level on our top-level command's LoggingMixin.
    //
    // In the main method, `LoggingMixin::executionStrategy` should be set as the execution strategy:
    // that will take the verbosity level that we stored in the top-level command's LoggingMixin
    // to configure logback before executing the command that the user specified.
    private static LoggingMixin getTopLevelCommandLoggingMixin(CommandLine.Model.CommandSpec commandSpec) {
        return ((MainApp) commandSpec.root().userObject()).loggingMixin;
    }

    /**
     * Sets the specified verbosity on the LoggingMixin of the top-level command.
     * @param verbosity the new verbosity value
     */
    @CommandLine.Option(names = {"-v", "--verbose"}, description = {
            "Increase verbosity."})
    public void setVerbose(boolean[] verbosity) {
        getTopLevelCommandLoggingMixin(mixee).verbosity = verbosity;
    }

    /**
     * Returns the verbosity from the LoggingMixin of the top-level command.
     * @return the verbosity value
     */
    public boolean[] getVerbosity() {
        return getTopLevelCommandLoggingMixin(mixee).verbosity;
    }

    public static int executionStrategy(CommandLine.ParseResult parseResult) {
        getTopLevelCommandLoggingMixin(parseResult.commandSpec()).configureLoggers();
        return new CommandLine.RunLast().execute(parseResult);
    }

    /**
     * Configures the logback ROOT_LOGGER level
     * <ul>
     *   <li>{@code -v} : enable DEBUG level</li>
     *   <li>(not specified) : enable INFO level</li>
     * </ul>
     */
    public void configureLoggers() {
        Level level = getTopLevelCommandLoggingMixin(mixee).calcLogLevel();
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(level);
    }

    private Level calcLogLevel() {
        return getVerbosity().length == 0 ? Level.INFO : Level.DEBUG;
    }
}