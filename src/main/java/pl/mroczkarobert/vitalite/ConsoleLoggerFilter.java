package pl.mroczkarobert.vitalite;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

import java.util.Arrays;
import java.util.List;

public class ConsoleLoggerFilter extends ch.qos.logback.core.filter.AbstractMatcherFilter {

    @Override
    public FilterReply decide(Object event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        LoggingEvent loggingEvent = (LoggingEvent) event;

        List<Level> eventsToKeep = Arrays.asList(Level.ERROR, Level.WARN, Level.INFO);
        if (eventsToKeep.contains(loggingEvent.getLevel())) {
            return FilterReply.NEUTRAL;

        } else {
            return FilterReply.DENY;
        }
    }
}