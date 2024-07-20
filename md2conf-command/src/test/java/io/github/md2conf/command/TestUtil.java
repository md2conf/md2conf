package io.github.md2conf.command;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.io.StringWriter;

public class TestUtil {

    public static CommandLine getCommandLine(StringWriter swOut, StringWriter swErr) {
        MainApp mainApp = new MainApp();
        CommandLine cmd = new CommandLine(mainApp);
        cmd.setOut(new PrintWriter(swOut));
        cmd.setErr(new PrintWriter(swErr));
        return cmd;
    }

    @SuppressWarnings("rawtypes")
    public static void addAppender(ListAppender<ILoggingEvent> listAppender, Class clazz) {
        Logger logger = (Logger) LoggerFactory.getLogger(clazz);
        listAppender.start();
        logger.addAppender(listAppender);
    }
}
