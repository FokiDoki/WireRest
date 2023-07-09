package com.wireguard.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class LogbackHandler extends UnsynchronizedAppenderBase<ILoggingEvent> implements SmartLifecycle {

    @Getter
    private final Queue<ILoggingEvent> logs = new LinkedList<>();

    @Value("${logging.api.max-elements}")
    @Getter
    private int maxElements;

    @Override
    protected void append(ILoggingEvent event)  {
        if (logs.size() >= maxElements) {
            logs.poll();
        }
        logs.add(event);
    }

    @Override
    public boolean isRunning() {
        return isStarted();
    }
}
