package com.wireguard.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor()
@Getter
public class LoggingEventDto {
    private final String level;
    private final String message;
    private final long timestamp;

    public static LoggingEventDto from(ILoggingEvent event) {
        return new LoggingEventDto(
                event.getLevel().toString(),
                event.getFormattedMessage(),
                event.getTimeStamp());
    }
}
