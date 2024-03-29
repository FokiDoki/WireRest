package com.wirerest.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Component
public class LogbackHandler extends UnsynchronizedAppenderBase<ILoggingEvent> implements SmartLifecycle {

    private final Queue<ILoggingEvent> logs = new LinkedBlockingQueue<>();

    @Value("${logging.api.max-elements}")
    @Getter
    private int maxElements;

    @Override
    protected void append(ILoggingEvent event) {
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
