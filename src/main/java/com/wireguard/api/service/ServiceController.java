package com.wireguard.api.service;

import com.wireguard.logs.LoggingEventDto;
import com.wireguard.logs.LogsDao;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service")
public class ServiceController {

    LogsDao logsDao;

    @Autowired
    public ServiceController(LogsDao logsDao) {
        this.logsDao = logsDao;
    }

    @GetMapping("logs")
    @Parameter(name = "from", description = "Return logs from timestamp. Format: Unix milliseconds (In case of 0, all logs will be returned)")
    @Parameter(name = "limit", description = "Limit the number of logs (In case of 0, all logs will be returned)")
    @Parameter(name = "levels", description = "List of levels to filter by, if empty all logs will be returned." +
            "Valid values are: TRACE, DEBUG, INFO, WARN, ERROR. Example: INFO,ERROR")
    public List<LoggingEventDto> getLogs(
            @RequestParam(value = "from", required = false, defaultValue = "0") long from,
            @RequestParam(value = "limit", required = false, defaultValue = "0") long limit,
            @RequestParam(value = "levels", required = false, defaultValue = "") List<String> levels
    ) {
        if (levels.isEmpty()) {
            return logsDao.getLogs(from, limit);
        } else {
            return logsDao.getLogs(from, limit, levels);
        }

    }
}
