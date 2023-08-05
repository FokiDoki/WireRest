package com.wirerest.logs;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogsHandlerConfiguration {

    private final LogbackHandler logbackHandler;

    @Autowired
    public LogsHandlerConfiguration(LogbackHandler logbackHandler) {
        this.logbackHandler = logbackHandler;
    }

    @PostConstruct
    public void activateLogbackHandler() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(logbackHandler);
    }
}
