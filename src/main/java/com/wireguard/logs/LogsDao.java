package com.wireguard.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Queue;

@Component
public class LogsDao {
    LogbackHandler logbackHandler;

    @Autowired
    public LogsDao(LogbackHandler logbackHandler) {
        this.logbackHandler = logbackHandler;
    }

    public Queue<ILoggingEvent> getLogs() {
        return logbackHandler.getLogs();
    }
}
