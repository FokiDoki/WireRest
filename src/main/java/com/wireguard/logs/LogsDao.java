package com.wireguard.logs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class LogsDao {
    LogbackHandler logbackHandler;

    @Autowired
    public LogsDao(LogbackHandler logbackHandler) {
        this.logbackHandler = logbackHandler;
    }

    public List<LoggingEventDto> getLogs() {
        return getLogsDtoStream()
                .collect(Collectors.toList());
    }

    public int getLogsSize() {
        return logbackHandler.getLogs().size();
    }

    private Stream<LoggingEventDto> getLogsDtoStream(){
        return logbackHandler.getLogs().stream()
                .map(LoggingEventDto::from);
    }

    public List<LoggingEventDto> getLogs(long from, long limit){
        limit = limit == 0 ? getLogsSize() : limit;
        return  getLogsDtoStream()
                .filter(loggingEventDto -> loggingEventDto.getTimestamp() >= from)
                .skip(getLogsSize() - limit)
                .collect(Collectors.toList());
    }
    public List<LoggingEventDto> getLogs(long from, long limit, List<String> levels){
        limit = limit == 0 ? getLogsSize() : limit;
        return  getLogsDtoStream()
                .filter(loggingEventDto -> loggingEventDto.getTimestamp() >= from)
                .filter(loggingEventDto -> levels.contains(loggingEventDto.getLevel()))
                .skip(getLogsSize() - limit)
                .collect(Collectors.toList());
    }

}
