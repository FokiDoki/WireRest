package com.wireguard.api.service;

import com.wireguard.api.AppError;
import com.wireguard.api.inteface.WgInterfaceDTO;
import com.wireguard.logs.LoggingEventDto;
import com.wireguard.logs.LogsDao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class ServiceLogsController {

    LogsDao logsDao;

    @Autowired
    public ServiceLogsController(LogsDao logsDao) {
        this.logsDao = logsDao;
    }

    @Operation(summary = "Get application logs",
            description = "The default limit is 1000 logs. " +
                    "The limit can be changed by passing the --logging.api.max-elements=<limit> parameter",
            tags = {"Service"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = {@Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LoggingEventDto.class)),
                                    examples = {
                                            @ExampleObject(name = "Logs",
                                                    ref = "#/components/examples/logs")
                                    })}),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = {@Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = AppError.class)),
                                    examples = {
                                            @ExampleObject(name = "BAD_LIMIT",
                                                    ref = "#/components/examples/logsLimit400")
                                    })})
            })
    @GetMapping("/v1/service/logs")
    @Parameter(name = "from", description = "Return logs from timestamp. Format: Unix milliseconds (In case of 0, all logs will be returned)")
    @Parameter(name = "limit", description = "Limit the number of logs (In case of 0, all logs will be returned)")
    @Parameter(name = "levels", description = "List of levels to filter by, if empty all logs will be returned." +
            "Valid values are: TRACE, DEBUG, INFO, WARN, ERROR. Example: INFO,ERROR")
    public List<LoggingEventDto> getLogs(
            @Min(0) @RequestParam(value = "from", required = false, defaultValue = "0") long from,
            @Min(0) @RequestParam(value = "limit", required = false, defaultValue = "0") long limit,
            @RequestParam(value = "levels", required = false, defaultValue = "") List<LogsDao.Level> levels
    ) {
        if (levels.isEmpty()) {
            return logsDao.getLogs(from, limit);
        } else {
            return logsDao.getLogs(from, limit, levels);
        }

    }


}
